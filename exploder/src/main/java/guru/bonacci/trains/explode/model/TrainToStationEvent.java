package guru.bonacci.trains.explode.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainToStationEvent extends TrainEvent {

    public int station;
    
    public TrainToStationEvent(TrainOnRoute trainOnRoute, int station) {
    	super(	trainOnRoute.train.id, 
    			trainOnRoute.route.route,
    			trainOnRoute.train.name,
    			trainOnRoute.train.moment,
    			trainOnRoute.train.lat,
    			trainOnRoute.train.lon);
    	this.station = station;
    }
}