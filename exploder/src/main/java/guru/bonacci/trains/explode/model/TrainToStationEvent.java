package guru.bonacci.trains.explode.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainToStationEvent {

    public String id;
    public int route;
    public String name;
    public double lat;
    public double lon;
    public int _goto;
    
    public TrainToStationEvent(TrainOnRoute trainOnRoute, int station) {
    	this.id = trainOnRoute.train.id; 
    	this.route = trainOnRoute.route.route;
    	this.name = trainOnRoute.train.name;
    	this.lat = trainOnRoute.train.lat;
    	this.lon = trainOnRoute.train.lon;
    	this._goto = station;
    }
}