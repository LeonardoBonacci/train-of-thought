package guru.bonacci.trains.arrival.streams;

import java.time.Duration;
import java.time.Instant;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ArrivalTimeProcessor {


    private static final String TRAIN_ARRIVAL_TIME_TOPIC = "train-arrival-time-events";
    
    private static final String TRAIN_TOPIC = "train-events";
    private static final String TRAIN_AT_STATION_TOPIC = "train-at-station-events";


    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<ArrivalTime> trainArrivalTimeSerde = new JsonbSerde<>(ArrivalTime.class);
        
        KStream<Integer, String> trainEvents = builder.stream(                                                       
                TRAIN_TOPIC,
                Consumed.with(Serdes.Integer(), Serdes.String())
        );

        KStream<Integer, String> atStationEvents = builder.stream(
        		TRAIN_AT_STATION_TOPIC, 
        		Consumed.with(Serdes.Integer(), Serdes.String()));

        trainEvents.join(atStationEvents,
                (leftV, rightV) -> {
                    String[] trainParts = leftV.split(";");
                    String[] atStationParts = rightV.split(";");

                	Instant trainInstant = Instant.parse(trainParts[0]);
        			Instant atStationInstant = Instant.parse(atStationParts[0]);

                	String name = trainParts[1];

                	Integer trainLocation = Integer.valueOf(trainParts[2]);
                	Integer stationLocation = Integer.valueOf(atStationParts[2]);

        			log.info(name + " takes " + Duration.between(trainInstant, atStationInstant).toMillis() + " ms from " + trainLocation + " until arrival at " + stationLocation);

        			return new ArrivalTime(name, Duration.between(trainInstant, atStationInstant), trainLocation, stationLocation);

                },
                JoinWindows.of(0).after(Duration.ofMillis(100000)),
                Joined.with(Serdes.Integer(), Serdes.String(), Serdes.String())
        )
        .peek((k, v) -> log.info(k + " <duration> " + v.getUntilArrival().toMillis()))
        .to(                                                          
        		TRAIN_ARRIVAL_TIME_TOPIC,
                Produced.with(Serdes.Integer(), trainArrivalTimeSerde)
        );

        return builder.build();
    }
}