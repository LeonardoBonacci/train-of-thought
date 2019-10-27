package guru.bonacci.trains.history.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Printed;

import com.github.davidmoten.geo.GeoHash;

import guru.bonacci.trains.model.homewardbound.HomewardTrain;
import guru.bonacci.trains.model.onmyway.SoLongTrain;
import guru.bonacci.trains.model.onmyway.WayTrain;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PredictorTopologyProducer {

    private static final String LIVE_TRAINS = "on-my-way";
	private static final String TRAIN_TIME_PREDICTIONS = "so-long";

    
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        GlobalKTable<String, SoLongTrain> predictions = builder.globalTable(
        		TRAIN_TIME_PREDICTIONS,
                Consumed.with(Serdes.String(), new JsonbSerde<>(SoLongTrain.class)));
        
//        builder.stream(
//                LIVE_TRAINS,
//                Consumed.with(Serdes.String(), new JsonbSerde<>(WayTrain.class))
//            )
//        	.selectKey((key, value) -> value.trainName.replaceAll("\\s","") + "-" 
//        								+ GeoHash.encodeHash(value.lat, value.lon, 7) + "-" 
//        								+ value.gotoName.replaceAll("\\s",""))
//        	.join(predictions, 
//        		 (train, prediction) -> new HomewardTrain(train.trainId, train.trainName, train.gotoId, prediction.togo),
//        		 Joined.with(Serdes.String(), new JsonbSerde<>(WayTrain.class), new JsonbSerde<>(SoLongTrain.class))
//        	)
//            .print(Printed.toSysOut());
        builder.stream(
                LIVE_TRAINS,
                Consumed.with(Serdes.String(), new JsonbSerde<>(WayTrain.class))
            )
	    	.selectKey((key, value) -> value.trainName.replaceAll("\\s","") 
	    							+ "-" + GeoHash.encodeHash(value.lat, value.lon, 7) 
	    							+ "-" + value.gotoName.replaceAll("\\s",""))
        	.join(predictions, 
        		 (trainId, train) -> trainId,
        		 (train, prediction) -> new HomewardTrain(train.trainId, train.trainName, train.gotoId, prediction.togo)
        	)
            .print(Printed.toSysOut());
        
        return builder.build();
    }
}
