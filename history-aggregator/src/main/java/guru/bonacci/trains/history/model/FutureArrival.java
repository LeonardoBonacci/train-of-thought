package guru.bonacci.trains.history.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class FutureArrival {

	public String id;
	public String name;
	public Double lat;
	public Double lon;

	public Long togo;
	public String station;
}