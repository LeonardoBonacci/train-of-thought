package guru.bonacci.trains.explode.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainToStationEvent extends TrainEvent {

    public Integer station;
    
    public TrainToStationEvent(TrainOnRoute trainOnRoute, Integer station) {
    	super(	trainOnRoute.train.id, 
    			trainOnRoute.route.route,
    			trainOnRoute.train.name,
    			trainOnRoute.train.moment,
    			trainOnRoute.train.lat,
    			trainOnRoute.train.lon);
    	this.station = station;
    }
}