package guru.bonacci.trains.history.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;

import com.github.davidmoten.geo.GeoHash;

import guru.bonacci.trains.model.onmyway.SoLongTrain;
import guru.bonacci.trains.model.onmyway.WayTrain;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ApplicationScoped
public class PredictorTopologyProcessor {


    private static final String TRAIN_TO_STATION = "on-my-way";
    private static final String TIME_PREDICTION = "so-long";
    

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<WayTrain> onMyWaySerde = new JsonbSerde<>(WayTrain.class);
        JsonbSerde<SoLongTrain> soLongSerde = new JsonbSerde<>(SoLongTrain.class);

        builder.stream(                                                       
                TRAIN_TO_STATION,
                Consumed.with(Serdes.String(), onMyWaySerde)
        )
        .selectKey((key, value) -> value.trainName + "-" + GeoHash.encodeHash(value.lat, value.lon, 7) + "-" + value.gotoName)
        .print(Printed.toSysOut());;

        return builder.build();
    }
}