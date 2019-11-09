package guru.bonacci.trains.arrivals.streams;

import java.time.Duration;

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

import guru.bonacci.trains.arrivals.model.ArrivalAt;
import guru.bonacci.trains.arrivals.model.FutureArrival;
import guru.bonacci.trains.arrivals.model.TrainEvent;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ArrivalTopologyProducer {

    private static final String UNTIL_ARRIVAL_TOPIC = "BACK_TO_THE_FUTURE";
    
    private static final String TRAINS_TOPIC = "I_AM_HERE";
    private static final String AT_STATION_TOPIC = "I_AM_HOME";


    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<TrainEvent> trainSerde = new JsonbSerde<>(TrainEvent.class);
        JsonbSerde<ArrivalAt> atStationSerde = new JsonbSerde<>(ArrivalAt.class);
        JsonbSerde<FutureArrival> futureSerde = new JsonbSerde<>(FutureArrival.class);

        
        KStream<String, TrainEvent> trains = builder.stream(                                                       
                TRAINS_TOPIC,
                Consumed.with(Serdes.String(), trainSerde)
        );

        KStream<String, ArrivalAt> atStations = builder.stream(
        		AT_STATION_TOPIC, 
        		Consumed.with(Serdes.String(), atStationSerde)
        );

        trains.join(atStations,
                (train, atStation) -> {
        			log.info("{} needs {} ms from [{},{}] until station {}", 
        					train.name, 
        					Duration.between(train.moment, atStation.MOMENT).toMillis(),
        					train.lat,
        					train.lon,
        					atStation.STATION);

        			return FutureArrival.builder()
        									.id(train.id)
        									.route(train.route)
        									.name(train.name)
        									.lat(train.lat)
        									.lon(train.lon)
        									.togo(Duration.between(train.moment, atStation.MOMENT).toMillis())
        									._goto(atStation.STATION)
        									.build();

                },
                JoinWindows.of(0).after(Duration.ofHours(1)), // join up till an hour in the future
                Joined.with(Serdes.String(), trainSerde, atStationSerde)
        )
        .peek((k,v) -> log.info("Still to go: {}", v))
        .to(                                                          
        		UNTIL_ARRIVAL_TOPIC,
                Produced.with(Serdes.String(), futureSerde)
        );

        return builder.build();
    }
}