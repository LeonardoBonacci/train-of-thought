package guru.bonacci.trains.history.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import com.github.davidmoten.geo.GeoHash;

import guru.bonacci.trains.history.model.AvgFutureArrival;
import guru.bonacci.trains.history.model.FutureArrival;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ApplicationScoped
public class HistoryProcessor {


    private static final String UNTIL_ARRIVAL = "BACK_TO_THE_FUTURE";
    private static final String AVERAGE_ARRIVAL_TIMES = "SO_LONG";
    

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<FutureArrival> togoSerde = new JsonbSerde<>(FutureArrival.class);
        JsonbSerde<AvgFutureArrival> avgSerde = new JsonbSerde<>(AvgFutureArrival.class);

        builder.stream(                                                       
                UNTIL_ARRIVAL,
                Consumed.with(Serdes.String(), togoSerde)
        )
        .selectKey((key, value) -> value.name + "-" + GeoHash.encodeHash(value.lat, value.lon, 7) + "-" + value.station)
        .peek((k,v) -> log.info(k + " >>> " + v))
        .groupByKey(Grouped.with(Serdes.String(), togoSerde))                                                
        .aggregate(                                                   
              AvgFutureArrival::new,
              (compositeId, value, aggregation) -> aggregation.updateFrom(value),
                Materialized.with(Serdes.String(), avgSerde)
        )
        .toStream()
        .peek((k,v) -> log.info(k + " <<< " + v))
        .to(                                                          
    		  AVERAGE_ARRIVAL_TIMES,
    		  Produced.with(Serdes.String(), avgSerde)
    	);

        return builder.build();
    }
}