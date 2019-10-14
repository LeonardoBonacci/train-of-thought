package org.acme.quarkus.sample.kafkastreams.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;


@ApplicationScoped
public class TrainStationSink {

    private static final String BUS_ARRIVAL_TIMES_AGGREGATED_TOPIC = "bus-arrival-times-aggregated";
    

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // train-events -> map key to busname/from
        // merges with ktable bus-arrival-times-aggregated
        // simplify aggregation to only contain shown fields
        // group by toLoc
        // create state store that can be queries by to
        return builder.build();
    }
}