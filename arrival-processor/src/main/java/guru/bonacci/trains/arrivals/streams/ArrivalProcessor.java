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
public class ArrivalProcessor {


    private static final String UNTIL_ARRIVAL = "back-to-the-future";
    
    private static final String TRAINS = "i-am-here";
    private static final String AT_STATIONS = "i-am-home";


    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<TrainEvent> trainSerde = new JsonbSerde<>(TrainEvent.class);
        JsonbSerde<ArrivalAt> atStationSerde = new JsonbSerde<>(ArrivalAt.class);
        
        KStream<String, TrainEvent> trains = builder.stream(                                                       
                TRAINS,
                Consumed.with(Serdes.String(), trainSerde)
        );

        KStream<String, ArrivalAt> atStations = builder.stream(
        		AT_STATIONS, 
        		Consumed.with(Serdes.String(), atStationSerde)
        );

        trains.join(atStations,
                (trainVal, atStationVal) -> {
        			log.info("{} needs {} ms from [{},{}] until station {}", 
        					trainVal.name, 
        					Duration.between(trainVal.moment, atStationVal.moment).toMillis(),
        					trainVal.lat,
        					trainVal.lon,
        					atStationVal.station);

        			return FutureArrival.builder()
        									.id(trainVal.id)
        									.name(trainVal.name)
        									.lat(trainVal.lat)
        									.lon(trainVal.lon)
        									.togo(Duration.between(trainVal.moment, atStationVal.moment).toMillis())
        									.station(atStationVal.station)
        									.build();

                },
                JoinWindows.of(0).after(Duration.ofMillis(10000)), //add a realistic value here!
                Joined.with(Serdes.String(), trainSerde, atStationSerde)
        )
        .peek((k,v) -> log.info("TOGO: " + v))
        .to(                                                          
        		UNTIL_ARRIVAL,
                Produced.with(Serdes.String(), new JsonbSerde<>(FutureArrival.class))
        );

        return builder.build();
    }
}