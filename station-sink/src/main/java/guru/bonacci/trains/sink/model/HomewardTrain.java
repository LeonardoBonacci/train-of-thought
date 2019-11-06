package guru.bonacci.trains.sink.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class HomewardTrain {

    public String id;
    public int route;
    public String name;
    
	public long togo; //in ms
	public int _goto; //station
}
