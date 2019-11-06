package guru.bonacci.trains.explode.model;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainToStationEvent {

    public String id;
    public Integer route;
    public String name;
    public Instant moment;
    public Double lat;
    public Double lon;
    public Integer station;
    
    public TrainToStationEvent(TrainOnRoute trainOnRoute, Integer station) {
    	this.id = trainOnRoute.train.id;
    	this.route = trainOnRoute.route.route;
    	this.name = trainOnRoute.train.name;
    	this.moment = trainOnRoute.train.moment;
    	this.lat = trainOnRoute.train.lat;
    	this.lon = trainOnRoute.train.lon;
    	this.station = station;
    }
}