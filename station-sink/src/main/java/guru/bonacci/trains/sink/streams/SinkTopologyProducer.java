package guru.bonacci.trains.sink.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import guru.bonacci.trains.sink.model.HomewardTrain;
import guru.bonacci.trains.sink.model.IncomingTrainAtStation;
import guru.bonacci.trains.sink.model.Station;
import guru.bonacci.trains.sink.model.StationAggr;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class SinkTopologyProducer {

    static final String STATIONS_STORE = "stations-store";

    private static final String TRAINS_TOPIC = "HOMEWARD_BOUND";
    private static final String STATIONS_TOPIC = "STATIONS";

    
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<HomewardTrain> trainSerde = new JsonbSerde<>(HomewardTrain.class);
        JsonbSerde<Station> stationSerde = new JsonbSerde<>(Station.class);
        JsonbSerde<IncomingTrainAtStation> incomingTrainSerde = new JsonbSerde<>(IncomingTrainAtStation.class);
        JsonbSerde<StationAggr> aggregationSerde = new JsonbSerde<>(StationAggr.class);
        
        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(STATIONS_STORE);

        GlobalKTable<Integer, Station> stations = builder.globalTable(
                STATIONS_TOPIC,
                Consumed.with(Serdes.Integer(), stationSerde));

        builder.stream(
                TRAINS_TOPIC,
                Consumed.with(Serdes.String(), trainSerde)
            )
        	.selectKey((key, value) -> value._goto) 
            .join(
                    stations,
                    (stationId, train) -> stationId,
                    (train, station) -> new IncomingTrainAtStation(train, station)
            )
            .groupByKey(Grouped.with(Serdes.Integer(), incomingTrainSerde))
            .aggregate( // aggregate by stationId
                    StationAggr::new,
                    (stationId, value, aggregation) -> aggregation.updateFrom(value),
                    Materialized.<Integer, StationAggr> as(storeSupplier)
                        .withKeySerde(Serdes.Integer())
                        .withValueSerde(aggregationSerde)
            )
            .toStream()
            .print(Printed.toSysOut());

        return builder.build();
    }
}
