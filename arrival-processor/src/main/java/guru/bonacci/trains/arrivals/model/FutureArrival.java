package guru.bonacci.trains.arrivals.model;

import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public class FutureArrival {

	private final String id;
	private final String name;
	private final Double lat;
	private final Double lon;

	private final Long togo;
	private final String _goto;
}