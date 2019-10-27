package guru.bonacci.trains.history.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;

import com.github.davidmoten.geo.GeoHash;

import guru.bonacci.trains.model.onmyway.WayTrain;
import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class PredictorTopologyProducer {

    private static final String LIVE_TRAINS = "on-my-way";
	private static final String TRAIN_TIME_PREDICTIONS = "so-long";

    
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, String> trainTimePredictions = builder.table(
        		TRAIN_TIME_PREDICTIONS,
//                Consumed.with(Serdes.String(), new JsonbSerde<>(SoLongTrain.class)));
                Consumed.with(Serdes.String(), Serdes.String()));
        trainTimePredictions.toStream()
        .print(Printed.toSysOut());
        
        builder.stream(
                LIVE_TRAINS,
                Consumed.with(Serdes.String(), new JsonbSerde<>(WayTrain.class))
            )
        	.selectKey((key, value) -> value.trainName.replaceAll("\\s","") + "-" 
        								+ GeoHash.encodeHash(value.lat, value.lon, 7) + "-" 
        								+ value.gotoName.replaceAll("\\s",""))
            .print(Printed.toSysOut());

        return builder.build();
    }
}
