package guru.bonacci.trains.sink.streams;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.common.collect.Streams;

import guru.bonacci.trains.sink.model.StationAggr;
import guru.bonacci.trains.sink.model.StationData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class InteractiveQueries {


	@ConfigProperty(name="hostname")
    String host;

    @Inject
    KafkaStreams streams;

    
    public List<PipelineMetadata> getMetaData() {
        return streams.allMetadataForStore(SinkTopologyProducer.STATIONS_STORE)
                .stream()
                .map(m -> new PipelineMetadata(
                        m.hostInfo().host() + ":" + m.hostInfo().port(),
                        m.topicPartitions()
                            .stream()
                            .map(TopicPartition::toString)
                            .collect(Collectors.toSet()))
                )
                .collect(Collectors.toList());
    }

    public StationDataResult getStationData(int id) {
        StreamsMetadata metadata = streams.metadataForKey(
                SinkTopologyProducer.STATIONS_STORE,
                id,
                Serdes.Integer().serializer()
        );

        if (metadata == null || metadata == StreamsMetadata.NOT_AVAILABLE) {
            log.warn("Found no metadata for key {}", id);
            return StationDataResult.notFound();
        }
        else if (metadata.host().equals(host)) {
            log.info("Found data for key {} locally", id);
            
            // for demo purposes we query the last three seconds (of several windows)
            // this way trains don't cling to stations when they have passed
            KeyValueIterator<Long, StationAggr> result1 = getStationStore().fetch(id, Instant.now().minusSeconds(2), Instant.now());
            StationAggr result = Streams.stream(result1).map(keyValue -> keyValue.value).reduce(new StationAggr(), StationAggr::merge);

            if (result != null) {
                return StationDataResult.found(StationData.from(result));
            }
            else {
                return StationDataResult.notFound();
            }
        }
        else {
            log.info("Found data for key {} on remote host {}:{}", id, metadata.host(), metadata.port());
            return StationDataResult.foundRemotely(metadata.host(), metadata.port());
        }
    }

    private ReadOnlyWindowStore<Integer, StationAggr> getStationStore() {
        while (true) {
            try {
                return streams.store(SinkTopologyProducer.STATIONS_STORE, QueryableStoreTypes.windowStore());
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}
