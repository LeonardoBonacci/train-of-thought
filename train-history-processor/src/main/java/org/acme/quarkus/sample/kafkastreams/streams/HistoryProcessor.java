package org.acme.quarkus.sample.kafkastreams.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import io.quarkus.kafka.client.serialization.JsonbSerde;


@ApplicationScoped
public class HistoryProcessor {

    private static final String TRAIN_ARRIVAL_TIME_TOPIC = "train-arrival-time-events";
    private static final String BUS_ARRIVAL_TIMES_AGGREGATED_TOPIC = "bus-arrival-times-aggregated";
    

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<ArrivalTime> busATSerde = new JsonbSerde<>(
        		ArrivalTime.class);
        
        JsonbSerde<ArrivalTimeAggr> aggregationSerde = new JsonbSerde<>(
        		ArrivalTimeAggr.class);


        builder.stream(                                                       
                TRAIN_ARRIVAL_TIME_TOPIC,
                Consumed.with(Serdes.String(), busATSerde)
        )
        .groupByKey()                                                 
        .aggregate(                                                   
                ArrivalTimeAggr::new,
                (compositeId, value, aggregation) -> aggregation.updateFrom(value)
        )
        .toStream()
         .map((compositeId, aggr) -> KeyValue.pair(aggr.busName + "/" + aggr.fromLoc, aggr))
        .to(                                                          
                BUS_ARRIVAL_TIMES_AGGREGATED_TOPIC,
                Produced.with(Serdes.String(), aggregationSerde)
        );

        return builder.build();
    }
}