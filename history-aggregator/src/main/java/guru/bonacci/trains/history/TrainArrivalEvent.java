package guru.bonacci.trains.history;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainArrivalEvent {

	public String id;
	public String name;
	public Double lat;
	public Double lon;

	public Long until;
	public String station;
}