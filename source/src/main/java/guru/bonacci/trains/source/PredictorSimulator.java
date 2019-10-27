package guru.bonacci.trains.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import guru.bonacci.trains.model.Station;
import guru.bonacci.trains.model.onmyway.WayTrain;
import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * For single service testing purposes
 */
@Slf4j
@ApplicationScoped
public class PredictorSimulator {

    private Random random = new Random();

    private List<Station> stations = Collections.unmodifiableList(
            Arrays.asList( 
            		Station.builder().id(6).name("Ngaio Station").lat(33.07).lon(-115.07).build(),
            		Station.builder().id(7).name("Crofton Downs Station").lat(33.08).lon(-115.08).build(),
            		Station.builder().id(8).name("Wellington Station").lat(33.09).lon(-115.09).build()
            ));

    private List<WayTrain> trains = Collections.unmodifiableList(
            Arrays.asList( 
            		new WayTrain("tr>" + random.nextInt(10), "some name", 33.05, -115.05, stations.get(0).id, stations.get(0).name),
            		new WayTrain("tr>" + random.nextInt(10), "some name", 33.05, -115.05, stations.get(1).id, stations.get(1).name),
            		new WayTrain("tr>" + random.nextInt(10), "some name", 33.05, -115.05, stations.get(2).id, stations.get(2).name)
            ));

    @Outgoing("on-my-way")
    public Flowable<KafkaMessage<String, String>> trainss() {
        return Flowable.interval(500, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .map(tick -> {
                	WayTrain t = trains.get(random.nextInt(trains.size()));
                    String train = 
    	                    "{ \"trainId\" : \"" + t.trainId + "\"" +
    	                    ", \"trainName\" : \"" + t.trainName + "\"" +
    	                    ", \"lat\" : " + t.lat + 
                            ", \"lon\" : " + t.lon + 
             				", \"gotoId\" : " + t.gotoId + 
             				", \"gotoName\" : \"" + t.gotoName + "\" }";

                    log.info("train: {}", train);
                    return KafkaMessage.of(t.trainId, train);
                });
    }
}
