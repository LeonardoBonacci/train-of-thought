package guru.bonacci.trains.generator;

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

/**
 * A bean producing random temperature data every second.
 * The values are written to a Kafka topic (i-am-here).
 * The Kafka configuration is specified in the application configuration.
 */
@Slf4j
@ApplicationScoped
public class ValuesGenerator {


    private List<Station> stations = Collections.unmodifiableList(
            Arrays.asList( 
                    Station.builder().id("jv").name("Johnsonville Station").lat(33.01).lon(-115.01).build(),
                    Station.builder().id("rar").name("Raroa Station").lat(33.02).lon(-115.02).build(),
                    Station.builder().id("kha").name("Khandallah Station").lat(33.03).lon(-115.03).build(),
                    Station.builder().id("boh").name("Box Hill Station").lat(33.04).lon(-115.04).build(),
                    Station.builder().id("sim").name("Simla Crescent Station").lat(33.05).lon(-115.05).build(),
                    Station.builder().id("awa").name("Awarua Street Station").lat(33.06).lon(-115.06).build(),
            		Station.builder().id("ng").name("Ngaio Station").lat(33.07).lon(-115.07).build(),
            		Station.builder().id("cdo").name("Crofton Downs Station").lat(33.08).lon(-115.08).build(),
            		Station.builder().id("wel").name("Wellington Station").lat(33.09).lon(-115.09).build()
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
	                             .add("stations") // collection name
	                             .add(st.name)
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
            		 						.name("JVL-WEL")
            		 						.lat(stations.get(0).lat - 0.02)
            		 						.lon(stations.get(0).lon + 0.02)
            		 						.build())
     		);

     @Outgoing("i-am-here")                             
     public Flowable<KafkaMessage<String, String>> generate() {
        return Flowable.interval(200, TimeUnit.MILLISECONDS)    
                .onBackpressureDrop()
                .map(tick -> {
                    Train train = moveAhead(trains.get(random.nextInt(trains.size())));
                    String payload = 
                    		 "{ \"id\" : " + train.id +
                             ", \"name\" : \"" + train.name + "\"" + 
                             ", \"moment\" : \"" + Instant.now().toEpochMilli() + "\"" + 
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
                                        .add("trains") // collection name
                                        .add(train.id)
                                        .add("POINT")
                                        .add(train.lat)
                                        .add(train.lon));
                    // ----------------------------------------------------

                    log.info("emitting train event: {}", payload);
                    return KafkaMessage.of(train.id, payload);
                });
    }

     private Train moveAhead(Train t) {
      	t.lat += 0.0002;
      	t.lon -= 0.0002;
      	return t;
      }
      

     @Builder
     private static class Train {

    	 private final String id;
    	 private final String name;
    	 private Double lat;
    	 private Double lon;
     }

     @Builder
     private static class Station {

    	 private final String id;
    	 private final String name;
    	 private final Double lat;
    	 private final Double lon;
     }
}