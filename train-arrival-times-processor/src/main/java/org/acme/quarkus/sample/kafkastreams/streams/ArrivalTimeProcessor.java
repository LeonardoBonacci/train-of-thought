package org.acme.quarkus.sample.kafkastreams.streams;

import java.time.Duration;
import java.time.Instant;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
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

        JsonbSerde<ArrivalTime> busATSerde = new JsonbSerde<>(ArrivalTime.class);

        
        KStream<Integer, String> busEvents = builder.stream(                                                       
                TRAIN_TOPIC,
                Consumed.with(Serdes.Integer(), Serdes.String())
        );

        KStream<Integer, String> busStopEvents = builder.stream(
        		TRAIN_AT_STATION_TOPIC, 
        		Consumed.with(Serdes.Integer(), Serdes.String()));

        busEvents.join(busStopEvents,
                (leftV, rightV) -> {
                    String[] busParts = leftV.split(";");
                    String[] busStopParts = rightV.split(";");

                	Instant busInstant = Instant.parse(busParts[0]);
        			Instant busStopInstant = Instant.parse(busStopParts[0]);

                	String busName = busParts[1];

                	String busLocation = busParts[2];
        			String busStopLocation = busStopParts[2];

        			log.info(busName + " takes " + Duration.between(busInstant, busStopInstant).toMillis() + " ms from " + busLocation + " until arrival at " + busStopLocation);

        			return new ArrivalTime(busName, Duration.between(busInstant, busStopInstant), Integer.valueOf(busLocation), Integer.valueOf(busStopLocation));

                },
                JoinWindows.of(0).after(Duration.ofMillis(100000)),
                Joined.with(Serdes.Integer(), Serdes.String(), Serdes.String())
        )
        .map((busId, busAT) -> KeyValue.pair(busAT.busName + "/" + busAT.fromLoc + "/" + busAT.toLoc, busAT))
        .peek((k, v) -> log.info(k + " <duration> " + v.untilArrival.toMillis()))
        .to(                                                          
        		TRAIN_ARRIVAL_TIME_TOPIC,
                Produced.with(Serdes.String(), busATSerde)
        );

        return builder.build();
    }
}