package guru.bonacci.trains.explode.model;

import java.util.HashSet;
import java.util.Set;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class Route {

    public int route;
    public Set<Integer> stations = new HashSet<>();
    

    public Route updateFrom(OnRoute incoming) {
        route = incoming.ROUTE;
        stations.add(incoming.station);
        
        return this;
    }
}