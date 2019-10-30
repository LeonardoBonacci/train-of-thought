package guru.bonacci.trains.sink.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class Station {

    public int id;
    public String name;
    
   	public double lat;
   	public double lon;
}
