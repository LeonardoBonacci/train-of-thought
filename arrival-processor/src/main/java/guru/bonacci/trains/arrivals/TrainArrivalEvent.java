package guru.bonacci.trains.arrivals;

import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public class TrainArrivalEvent {

	private final String id;
	private final String name;
	private final Double lat;
	private final Double lon;

	private final Long togo;
	private final String station;
}