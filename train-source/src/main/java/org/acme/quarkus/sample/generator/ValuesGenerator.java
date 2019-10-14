package org.acme.quarkus.sample.generator;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;

/**
 * A bean producing random temperature data every second.
 * The values are written to a Kafka topic (bus-values).
 * Another topic contains the name of weather stations (weather-stations).
 * The Kafka configuration is specified in the application configuration.
 */
@ApplicationScoped
public class ValuesGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(ValuesGenerator.class);

    private Random random = new Random();

    private List<Bus> stations = Collections.unmodifiableList(
            Arrays.asList(
                    new Bus(1, "25"),
                    new Bus(2, "26"),
                    new Bus(3, "other")
            ));


    @Outgoing("train-events")                             
    public Flowable<KafkaMessage<Integer, String>> generate() {

    	final Map<Bus, Integer> stepCounter = new HashMap<>();
    	stations.forEach(b -> stepCounter.put(b, 0));
    	
        return Flowable.interval(500, TimeUnit.MILLISECONDS)    
                .onBackpressureDrop()
                .map(tick -> {
                    Bus bus = stations.get(random.nextInt(stations.size()));
                    stepCounter.compute(bus, (b, counter)-> new Integer(counter.intValue() + 1));

                    String payload = Instant.now() + ";" + bus.name + ";" + stepCounter.get(bus);
                    LOG.info("bus: {}", payload);
                    return KafkaMessage.of(bus.id, payload);
                });
    }


    private static class Bus {

        int id;
        String name;

        public Bus(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}