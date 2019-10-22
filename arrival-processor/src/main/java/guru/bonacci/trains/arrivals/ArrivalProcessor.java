package guru.bonacci.trains.arrivals;

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

import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ArrivalProcessor {


    private static final String TRAIN_ARRIVAL_TIME_TOPIC = "back-to-the-future";
    
    private static final String TRAINS = "i-am-here";
    private static final String AT_STATIONS = "i-am-home";


    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<TrainEvent> trainSerde = new JsonbSerde<>(TrainEvent.class);
        JsonbSerde<ArrivedAt> atStationSerde = new JsonbSerde<>(ArrivedAt.class);
        
        KStream<String, TrainEvent> trains = builder.stream(                                                       
                TRAINS,
                Consumed.with(Serdes.String(), trainSerde)
        );

        KStream<String, ArrivedAt> atStations = builder.stream(
        		AT_STATIONS, 
        		Consumed.with(Serdes.String(), atStationSerde));

        trains.join(atStations,
                (trainVal, atStationVal) -> {
        			log.info("{} needs {} ms from [{},{}] until station...?", 
        					trainVal.name, 
        					Duration.between(trainVal.moment, atStationVal.moment).toMillis(),
        					trainVal.lat,
        					trainVal.lon);

        			return new TrainArrivalEvent(trainVal.name, trainVal.moment, atStationVal.moment);

                },
                JoinWindows.of(0).after(Duration.ofMillis(10000)), //add a realistic value here!
                Joined.with(Serdes.String(), trainSerde, atStationSerde)
        );
//        .to(                                                          
//        		TRAIN_ARRIVAL_TIME_TOPIC,
//                Produced.with(Serdes.Integer(), trainArrivalTimeSerde)
//        );

        return builder.build();
    }
}