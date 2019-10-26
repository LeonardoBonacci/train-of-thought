package org.acme.quarkus.sample.kafkastreams.streams;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.quarkus.sample.kafkastreams.model.StationAggregation;
import org.acme.quarkus.sample.kafkastreams.model.StationData;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class InteractiveQueries {


	@ConfigProperty(name="hostname")
    String host;

    @Inject
    KafkaStreams streams;

    
    public List<PipelineMetadata> getMetaData() {
        return streams.allMetadataForStore(TopologyProducer.STATIONS_STORE)
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
                TopologyProducer.STATIONS_STORE,
                id,
                Serdes.Integer().serializer()
        );

        if (metadata == null || metadata == StreamsMetadata.NOT_AVAILABLE) {
            log.warn("Found no metadata for key {}", id);
            return StationDataResult.notFound();
        }
        else if (metadata.host().equals(host)) {
            log.info("Found data for key {} locally", id);
            StationAggregation result = getStationStore().get(id);

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

    private ReadOnlyKeyValueStore<Integer, StationAggregation> getStationStore() {
        while (true) {
            try {
                return streams.store(TopologyProducer.STATIONS_STORE, QueryableStoreTypes.keyValueStore());
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}
