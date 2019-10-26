package guru.bonacci.trains.sink.model;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class StationAggregation {

    public int stationId;
    public String stationName;
    
    // no need for synchronization just yet...
//TODO use: org.apache.commons.collections4.map.PassiveExpiringMap
    public Map<String, Train> trains = new HashMap<>();
    
    public StationAggregation updateFrom(IncomingTrainAtStation incoming) {
        stationId = incoming.stationId;
        stationName = incoming.stationName;

        trains.put(incoming.trainId, new Train(incoming.trainName, incoming.togo));
        
        return this;
    }
}