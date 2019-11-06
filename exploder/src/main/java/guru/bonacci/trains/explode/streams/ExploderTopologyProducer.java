package guru.bonacci.trains.explode.streams;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import guru.bonacci.trains.explode.model.OnRoute;
import guru.bonacci.trains.explode.model.Route;
import guru.bonacci.trains.explode.model.TrainEvent;
import guru.bonacci.trains.explode.model.TrainOnRoute;
import guru.bonacci.trains.explode.model.TrainToStationEvent;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ApplicationScoped
public class ExploderTopologyProducer {

    private static final String ON_MY_WAY_TOPIC = "ON_MY_WAY";

    private static final String TRAINS_TOPIC = "I_AM_HERE";
    private static final String ON_ROUTE_TOPIC = "ON_ROUTE";


    @SuppressWarnings("deprecation")
	@Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<TrainEvent> trainSerde = new JsonbSerde<>(TrainEvent.class);
        JsonbSerde<OnRoute> onRouteSerde = new JsonbSerde<>(OnRoute.class);
        JsonbSerde<Route> routeSerde = new JsonbSerde<>(Route.class);
        JsonbSerde<TrainToStationEvent> trainToStationEventSerde = new JsonbSerde<>(TrainToStationEvent.class);
        
        
        KStream<String, TrainEvent> trains = builder.stream(
	                TRAINS_TOPIC,
	                Consumed.with(Serdes.String(), trainSerde))
            	.selectKey((k,v) -> String.valueOf(v.route));

        //FIXME for (to me) unknown reasons keys from KSQL generated streams are always Varchars
        KStream<String, Route> routes = builder.stream(
		                ON_ROUTE_TOPIC,
		                Consumed.with(Serdes.String(), onRouteSerde))
	            .groupByKey()                                                
		        .aggregate(
			        	Route::new,
		                (routeId, value, aggregation) -> aggregation.updateFrom(value),
		        		Materialized.with(Serdes.String(), routeSerde))
		        .toStream();
        
        	trains.join(
		        		routes, 
		        		(train, route) -> new TrainOnRoute(train, route),
		        		JoinWindows.of(0).before(Duration.ofDays(1)), // whatever stations were passed since yesterday
		        		Joined.with(Serdes.String(), trainSerde, routeSerde)
	        	)
        		.peek((k,v) -> log.info(k + " >>> " + v))
        		.flatMap((routeId, trainOnRoute) -> {
		                List<KeyValue<String, TrainToStationEvent>> result = new ArrayList<>(trainOnRoute.route.stations.size());
		
		                for(Integer station : trainOnRoute.route.stations) 
		                    result.add(new KeyValue<>(trainOnRoute.train.id, new TrainToStationEvent(trainOnRoute, station)));
		                
		                return result;
	        	})
        		.to(
	        			ON_MY_WAY_TOPIC,
	        			Produced.with(Serdes.String(), trainToStationEventSerde)
        		);
        
        return builder.build();
    }
}