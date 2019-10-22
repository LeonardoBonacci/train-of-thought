package guru.bonacci.trains.arrivals;

import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public class TrainArrivalEvent {

	public String id;
	public String name;
	public Double lat;
	public Double lon;

	public Long until;
	public String station;
}