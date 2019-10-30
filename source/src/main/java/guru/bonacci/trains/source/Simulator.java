package guru.bonacci.trains.source;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.codec.StringCodec;
import com.lambdaworks.redis.output.StatusOutput;
import com.lambdaworks.redis.protocol.CommandArgs;
import com.lambdaworks.redis.protocol.CommandType;

import io.quarkus.runtime.StartupEvent;
import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class Simulator {

	private final static double START_LAT = 33.00;
	private final static double START_LON = -115.00;
	
    private List<Station> stations = Collections.unmodifiableList(
            Arrays.asList( 
                    Station.builder().id(0).name("Johnsonville Station").lat(33.01).lon(-115.01).build(),
                    Station.builder().id(1).name("Raroa Station").lat(33.02).lon(-115.02).build(),
                    Station.builder().id(2).name("Khandallah Station").lat(33.03).lon(-115.03).build(),
                    Station.builder().id(3).name("Box Hill Station").lat(33.04).lon(-115.04).build(),
                    Station.builder().id(4).name("Simla Crescent Station").lat(33.05).lon(-115.05).build(),
                    Station.builder().id(5).name("Awarua Street Station").lat(33.06).lon(-115.06).build(),
            		Station.builder().id(6).name("Ngaio Station").lat(33.07).lon(-115.07).build(),
            		Station.builder().id(7).name("Crofton Downs Station").lat(33.08).lon(-115.08).build(),
            		Station.builder().id(8).name("Wellington Station").lat(33.09).lon(-115.09).build()
            ));


     void onStart(@Observes StartupEvent ev) {
         log.info("Application has started");
         RedisClient client = RedisClient.create("redis://tile-server:9851");
         StatefulRedisConnection<String, String> connection = client.connect();
         RedisCommands<String, String> sync = connection.sync();
         StringCodec codec = StringCodec.UTF8;

         stations.forEach(st -> {
	         sync.dispatch(CommandType.SET,
	                     new StatusOutput<>(codec), new CommandArgs<>(codec)
	                             .add("stations") // internal collection name
	                             .add(st.id)
	                             .add("POINT")
	                             .add(st.lat)
	                             .add(st.lon));
	
	         String tileResp = sync.dispatch(CommandType.GET,
	                 new StatusOutput<>(codec), new CommandArgs<>(codec)
	                         .add("stations")
	                         .add(st.name));
	         log.info(tileResp);
         });
     }


     Random random = new Random();

     List<Train> trains = Collections.unmodifiableList(
             Arrays.asList(Train.builder().id(UUID.randomUUID().toString())
            		 						.route(66)
            		 						.name("JVL-WEL")
            		 						.lat(START_LAT)
            		 						.lon(START_LON)
            		 						.build())
     		);

     @Outgoing("I_AM_HERE")                             
     public Flowable<KafkaMessage<String, String>> trainEvents() {
        return Flowable.interval(500, TimeUnit.MILLISECONDS)    
                .onBackpressureDrop()
                .map(tick -> {
                    Train train = moveAhead(trains.get(random.nextInt(trains.size())));
                    String payload = 
                     		 "{ \"id\" : \"" + train.id + "\"" + 
                             ", \"route\" : " + train.route +  
                             ", \"name\" : \"" + train.name + "\"" + 
							 ", \"moment\" : \"" + Instant.now() + "\"" + 
                             ", \"lat\" : " + train.lat + 
                             ", \"lon\" : " + train.lon + "}";

                    // ----------------------------------------------------
                    // FOR NOW WE SEND THE TRAIN EVENT STRAIGHT TO THE tile38-server
                    RedisClient client = RedisClient.create("redis://tile-server:9851");
                    StatefulRedisConnection<String, String> connection = client.connect();
                    RedisCommands<String, String> sync = connection.sync();

                    StringCodec codec = StringCodec.UTF8;
                    sync.dispatch(CommandType.SET,
                                new StatusOutput<>(codec), new CommandArgs<>(codec)
                                        .add("trains") // internal collection name
                                        .add(train.id)
                                        .add("FIELDS")
                                        .add("route")
                                        .add(train.route)
                                        .add("POINT")
                                        .add(train.lat)
                                        .add(train.lon));
                    // ----------------------------------------------------

                    log.info("emitting train event: {}", payload);
                    return KafkaMessage.of(train.id, payload);
                });
     }

     private Train moveAhead(Train t) {
		 t.lat += 0.0003;
		 t.lon -= 0.0003;
    	 return t;
     }
      
     @Builder
     private static class Train {

    	 private final String id;
    	 private final Integer route;
    	 private final String name;
    	 private Double lat;
    	 private Double lon;
     }

     @Builder
     private static class Station {

    	 private final Integer id;
    	 private final String name;
    	 private final Double lat;
    	 private final Double lon;
     }
}