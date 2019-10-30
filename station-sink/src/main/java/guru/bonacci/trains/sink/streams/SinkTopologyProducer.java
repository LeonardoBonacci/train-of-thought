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

import guru.bonacci.trains.model.Station;
import guru.bonacci.trains.model.homewardbound.HomewardTrain;
import guru.bonacci.trains.sink.model.IncomingTrainAtStation;
import guru.bonacci.trains.sink.model.StationAggregation;
import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class SinkTopologyProducer {

    static final String STATIONS_STORE = "stations-store";

    private static final String TRAINS = "HOMEWARD_BOUND";
    private static final String STATIONS = "STATIONS";

    
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<HomewardTrain> trainSerde = new JsonbSerde<>(HomewardTrain.class);
        JsonbSerde<Station> stationSerde = new JsonbSerde<>(Station.class);
        JsonbSerde<StationAggregation> aggregationSerde = new JsonbSerde<>(StationAggregation.class);

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(STATIONS_STORE);

        GlobalKTable<Integer, Station> stations = builder.globalTable(
                STATIONS,
                Consumed.with(Serdes.Integer(), stationSerde));

        builder.stream(
                TRAINS,
                Consumed.with(Serdes.String(), trainSerde)
            )
        	.selectKey((key, value) -> value.gotoId)
            .join(
                    stations,
                    (stationId, train) -> stationId,
                    (train, station) -> new IncomingTrainAtStation(train, station)
            )
            .groupByKey(Grouped.with(Serdes.Integer(),  new JsonbSerde<>(IncomingTrainAtStation.class)))
            .aggregate(
                    StationAggregation::new,
                    (stationId, value, aggregation) -> aggregation.updateFrom(value),
                    Materialized.<Integer, StationAggregation> as(storeSupplier)
                        .withKeySerde(Serdes.Integer())
                        .withValueSerde(aggregationSerde)
            )
            .toStream()
            .print(Printed.toSysOut());

        return builder.build();
    }
}
