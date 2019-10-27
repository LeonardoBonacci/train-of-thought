package guru.bonacci.trains.history.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;

@ApplicationScoped
public class TopologyProducer {

    private static final String INCOMING_TRAINS = "on-my-way";

    
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        builder.stream(
                INCOMING_TRAINS,
                Consumed.with(Serdes.String(), Serdes.String())
            )
            .print(Printed.toSysOut());

        return builder.build();
    }
}
