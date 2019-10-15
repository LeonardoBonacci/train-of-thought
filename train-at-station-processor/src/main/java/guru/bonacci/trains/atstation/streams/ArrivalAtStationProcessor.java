package guru.bonacci.trains.atstation.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ArrivalAtStationProcessor {


	private static final String TRAIN_EVENTS_TOPIC = "train-events";
    private static final String TRAIN_AT_STATION_EVENTS_TOPIC = "train-at-station-events";

    
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        builder.stream(             			                                          
    				TRAIN_EVENTS_TOPIC,
                    Consumed.with(Serdes.Integer(), Serdes.String())
            )
			.filter((trainId, payload) -> {
	            Integer counter = Integer.parseInt(payload.split(";")[2]);
	            return counter % 5 == 0; // every fifth step is a station for now...
			})
			.peek((key, payload) -> log.info("train with id " + key + " < arrived at train station > " + Integer.parseInt(payload.split(";")[2])))
	        .to(                                                          
	        		TRAIN_AT_STATION_EVENTS_TOPIC,
	                Produced.with(Serdes.Integer(), Serdes.String())
	        );

        return builder.build();
    }
}