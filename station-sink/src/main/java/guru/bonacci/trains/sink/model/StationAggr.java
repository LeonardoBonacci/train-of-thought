package guru.bonacci.trains.sink.model;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class StationAggr {

	public int stationId;
	public String stationName;

	public Map<String, TrainForAggr> trains = new HashMap<>();
	
	
	public StationAggr updateFrom(IncomingTrainAtStation incoming) {
		stationId = incoming._goto;
		stationName = incoming.gotoName;

		trains.put(incoming.id, new TrainForAggr(incoming.id, incoming.togo));

		return this;
	}
	
	public StationAggr merge(StationAggr other) {
		stationId = other.stationId;
		stationName = other.stationName;

		trains.putAll(other.trains);

		return this;
	}
}