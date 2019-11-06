package guru.bonacci.trains.explode.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainOnRoute {

    public TrainEvent train;
    public Route route;
    
    public TrainOnRoute(TrainEvent train, Route route) {
    	this.train = train;
    	this.route = route;
    }
}