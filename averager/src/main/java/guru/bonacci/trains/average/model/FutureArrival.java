package guru.bonacci.trains.average.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class FutureArrival {

	public String id;
	public int route;
	public String name;
	public double lat;
	public double lon;

	public long togo; //in ms
	public int _goto; //station

}