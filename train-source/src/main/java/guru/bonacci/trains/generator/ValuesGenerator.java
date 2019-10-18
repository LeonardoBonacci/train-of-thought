package guru.bonacci.trains.generator;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import com.google.common.collect.Maps;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A bean producing random temperature data every second.
 * The values are written to a Kafka topic (train-events).
 * Another topic contains the name of the train stations (train-stations).
 * The Kafka configuration is specified in the application configuration.
 */
@Slf4j
@ApplicationScoped
public class ValuesGenerator {


    private Random random = new Random();

    private List<Station> stations = Collections.unmodifiableList(
            Arrays.asList(
                    new Station(1, "0"),
                    new Station(2, "5"),
                    new Station(3, "10"),
                    new Station(4, "15"),
                    new Station(5, "20"),
                    new Station(6, "25"),
                    new Station(7, "30"),
                    new Station(8, "35"),
                    new Station(9, "40"),
                    new Station(10, "42")
            ));


     private List<Train> trains = Collections.unmodifiableList(
            Arrays.asList(
                    new Train(1, "To nowhere"),
                    new Train(2, "To heaven"),
                    new Train(3, "To hell"),
                    new Train(4, "To left"),
                    new Train(5, "To right"),
                    new Train(6, "To straight ahead"),
                    new Train(7, "To backwards"),
                    new Train(8, "To up"),
                    new Train(9, "To down"),
                    new Train(10, "To inside"),
                    new Train(11, "Trem das onze")
            ));

    @Outgoing("train-events")                             
    public Flowable<KafkaMessage<Integer, String>> generate() {

    	final Map<Train, Integer> stepCounter = Maps.newHashMap();
    	trains.forEach(tr -> stepCounter.put(tr, 0));
    	
        return Flowable.interval(500, TimeUnit.MILLISECONDS)    
                .onBackpressureDrop()
                .map(tick -> {
                    Train train = trains.get(random.nextInt(trains.size()));

                    stepCounter.compute(train, (b, counter)-> new Integer(counter.intValue() + 1));
                    String payload = 
                    		 "{ \"id\" : " + train.id +
                             ", \"name\" : \"" + train.name + "\"" + 
                             ", \"moment\" : \"" + Instant.now().toEpochMilli() + "\"" + 
                             ", \"location\" : \"" + stepCounter.get(train) + "\" }";

                    log.info("emitting train event: {}", payload);
                    return KafkaMessage.of(train.id, payload);
                });
    }

    @Outgoing("train-stations")                               
    public Flowable<KafkaMessage<Integer, String>> trainStations() {
        List<KafkaMessage<Integer, String>> stationsAsJson = stations.stream()
            .map(s -> KafkaMessage.of(
                    s.id,
                    "{ \"id\" : " + s.id +
                    ", \"location\" : \"" + s.location + "\" }"))
            .collect(Collectors.toList());

        return Flowable.fromIterable(stationsAsJson);
    };
    
    
    @RequiredArgsConstructor
    private static class Train {

        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    private static class Station {

        private final int id;
        private final String location; 
    }
}