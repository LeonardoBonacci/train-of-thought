package guru.bonacci.trains.history;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;

import io.quarkus.kafka.client.serialization.JsonbSerde;


@ApplicationScoped
public class HistoryProcessor {


    private static final String UNTIL_ARRIVAL = "back-to-the-future";
    private static final String AVERAGE_ARRIVAL_TIMES = "average-arrival-times";
    

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<TrainArrivalEvent> togoSerde = new JsonbSerde<>(TrainArrivalEvent.class);
        
        builder.stream(                                                       
                UNTIL_ARRIVAL,
                Consumed.with(Serdes.String(), togoSerde)
        ).print(Printed.<String, TrainArrivalEvent>toSysOut().withLabel("BLABLABLA"));

//        .groupByKey()                                                 
//        .aggregate(                                                   
//                ArrivalTimeAggr::new,
//                (compositeId, value, aggregation) -> aggregation.updateFrom(value)
//        )
//        .toStream()
//         .map((compositeId, aggr) -> KeyValue.pair(aggr.busName + "/" + aggr.fromLoc, aggr))
//        .to(                                                          
//                BUS_ARRIVAL_TIMES_AGGREGATED_TOPIC,
//                Produced.with(Serdes.String(), aggregationSerde)
//        );

        return builder.build();
    }
}