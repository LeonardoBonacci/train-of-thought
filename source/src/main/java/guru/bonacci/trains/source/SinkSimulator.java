package guru.bonacci.trains.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * For testing purposes
 */
@Slf4j
@ApplicationScoped
public class SinkSimulator {

    private Random random = new Random();

    private List<SimStation> stations = Collections.unmodifiableList(
            Arrays.asList( 
                    SimStation.builder().id(0).name("Johnsonville Station").lat(33.01).lon(-115.01).build(),
                    SimStation.builder().id(1).name("Raroa Station").lat(33.02).lon(-115.02).build(),
                    SimStation.builder().id(2).name("Khandallah Station").lat(33.03).lon(-115.03).build(),
                    SimStation.builder().id(3).name("Box Hill Station").lat(33.04).lon(-115.04).build(),
                    SimStation.builder().id(4).name("Simla Crescent Station").lat(33.05).lon(-115.05).build(),
                    SimStation.builder().id(5).name("Awarua Street Station").lat(33.06).lon(-115.06).build(),
            		SimStation.builder().id(6).name("Ngaio Station").lat(33.07).lon(-115.07).build(),
            		SimStation.builder().id(7).name("Crofton Downs Station").lat(33.08).lon(-115.08).build(),
            		SimStation.builder().id(8).name("Wellington Station").lat(33.09).lon(-115.09).build()
            ));

    @Outgoing("incoming-trains")
    public Flowable<KafkaMessage<String, String>> trains() {
        return Flowable.interval(500, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .map(tick -> {
                    SimStation station = stations.get(0); //random.nextInt(stations.size()));
                    SimTrain t = new SimTrain("tr>" + random.nextInt(10), "some name", station.id, random.nextLong());
                    String train = 
    	                    "{ \"trainId\" : \"" + t.trainId + "\"" +
    	                    ", \"trainName\" : \"" + t.trainName + "\"" +
             				", \"_goto\" : " + t._goto + 
             				", \"togo\" : " + t.togo + " }";

                    log.info("station: {}, train: {}", station.name, train);
                    return KafkaMessage.of(t.trainId, train);
                });
    }

    @Outgoing("train-stations")
    public Flowable<KafkaMessage<Integer, String>> stations() {
        List<KafkaMessage<Integer, String>> stationsAsJson = stations.stream()
            .map(s -> KafkaMessage.of(s.id, "{ \"id\" : " + s.id + ", \"name\" : \"" + s.name + "\" }"))
            .collect(Collectors.toList());

        return Flowable.fromIterable(stationsAsJson);
    };

    @Builder
    private static class SimStation {

	   	private final int id;
	   	private final String name;
	   	private final double lat;
	   	private final double lon;
    }
    
    @Builder 
    private static class SimTrain {

   		private final String trainId;
   		private final String trainName;

   		private final int _goto;
   		private final long togo;
	}

}
