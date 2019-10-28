package guru.bonacci.trains.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import guru.bonacci.trains.model.Station;
import guru.bonacci.trains.model.homewardbound.HomewardTrain;
import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * For single service testing purposes
 */
@Slf4j
@ApplicationScoped
public class SinkSimulator {

    private Random random = new Random();

    private List<Station> stations = Collections.unmodifiableList(
            Arrays.asList( 
                    Station.builder().id(0).name("Johnsonville Station").lat(33.01).lon(-115.01).build()
            ));

//    @Outgoing("homeward-bound")
//    public Flowable<KafkaMessage<String, String>> trains() {
//        return Flowable.interval(500, TimeUnit.MILLISECONDS)
//                .onBackpressureDrop()
//                .map(tick -> {
//                    Station station = stations.get(0); 
//                    HomewardTrain t = new HomewardTrain("tr>" + random.nextInt(10), "some name", station.id, random.nextLong());
//                    String train = 
//    	                    "{ \"trainId\" : \"" + t.trainId + "\"" +
//    	                    ", \"trainName\" : \"" + t.trainName + "\"" +
//             				", \"gotoId\" : " + t.gotoId + 
//             				", \"togo\" : " + t.togo + " }";
//
//                    log.info("station: {}, train: {}", station.name, train);
//                    return KafkaMessage.of(t.trainId, train);
//                });
//    }

//    @Outgoing("stations")
    public Flowable<KafkaMessage<Integer, String>> stations() {
        List<KafkaMessage<Integer, String>> stationsAsJson = stations.stream()
            .map(s -> KafkaMessage.of(s.id, "{ \"id\" : " + s.id + ", \"name\" : \"" + s.name + "\" }"))
            .collect(Collectors.toList());

        return Flowable.fromIterable(stationsAsJson);
    };
}
