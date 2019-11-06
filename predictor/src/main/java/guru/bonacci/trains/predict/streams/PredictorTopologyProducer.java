package guru.bonacci.trains.predict.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Produced;

import com.github.davidmoten.geo.GeoHash;

import guru.bonacci.trains.predict.model.HomewardTrain;
import guru.bonacci.trains.predict.model.SoLongTrain;
import guru.bonacci.trains.predict.model.WayTrain;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PredictorTopologyProducer {

    private static final String LIVE_TRAINS_TOPIC = "ON_MY_WAY";
	private static final String TIME_PREDICTIONS_TOPIC = "SO_LONG";
    private static final String HOMEWARD_TOPIC = "HOMEWARD_BOUND";
    
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<SoLongTrain> predictionsSerde = new JsonbSerde<>(SoLongTrain.class);
        JsonbSerde<WayTrain> waySerde = new JsonbSerde<>(WayTrain.class);
        JsonbSerde<HomewardTrain> homewardSerde = new JsonbSerde<>(HomewardTrain.class);

        //TODO this should be a KTable
        GlobalKTable<String, SoLongTrain> predictions = builder.globalTable(
        		TIME_PREDICTIONS_TOPIC,
                Consumed.with(Serdes.String(), predictionsSerde));

        builder.stream(
                LIVE_TRAINS_TOPIC,
                Consumed.with(Serdes.String(), waySerde)
            )
	    	.selectKey((key, value) -> String.format("%d/%s/%d",
							    			value.route,
							    			GeoHash.encodeHash(value.lat, value.lon, 7), 
							    			value._goto))
        	.join(predictions, 
        		 (trainId, train) -> trainId, 
        		 (train, prediction) -> new HomewardTrain(train.id, train.route, train.name, prediction.avg, train._goto)
        	)
        	.selectKey((k,train) -> train.id)
        	.peek((k,v) -> log.info(k + " >>> " + v))
        	.to(
        			HOMEWARD_TOPIC, 
        			Produced.with(Serdes.String(), homewardSerde)
			);

        return builder.build();
    }
}
