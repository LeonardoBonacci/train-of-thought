package guru.bonacci.trains.generator;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import com.google.common.collect.Maps;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A bean producing random temperature data every second.
 * The values are written to a Kafka topic (train-values).
 * TODO: Another topic contains the name of weather stations (weather-stations).
 * The Kafka configuration is specified in the application configuration.
 */
@Slf4j
@ApplicationScoped
public class ValuesGenerator {


    private Random random = new Random();

    private List<Train> stations = Collections.unmodifiableList(
            Arrays.asList(
                    new Train(1, "To nowhere"),
                    new Train(2, "To heaven"),
                    new Train(3, "To hell"),
                    new Train(11, "Trem das onze")
            ));


    @Outgoing("train-events")                             
    public Flowable<KafkaMessage<Integer, String>> generate() {

    	final Map<Train, Integer> stepCounter = Maps.newHashMap();
    	stations.forEach(b -> stepCounter.put(b, 0));
    	
        return Flowable.interval(500, TimeUnit.MILLISECONDS)    
                .onBackpressureDrop()
                .map(tick -> {
                    Train train = stations.get(random.nextInt(stations.size()));

                    stepCounter.compute(train, (b, counter)-> new Integer(counter.intValue() + 1));
                    String payload = Instant.now() + ";" + train.name + ";" + stepCounter.get(train);

                    log.info("emitting train event: {}", payload);
                    return KafkaMessage.of(train.id, payload);
                });
    }

    
    @RequiredArgsConstructor
    private static class Train {

        private final int id;
        private final String name;
    }
}