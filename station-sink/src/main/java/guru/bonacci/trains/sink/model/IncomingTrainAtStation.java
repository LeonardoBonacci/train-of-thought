package guru.bonacci.trains.sink.model;

import com.google.common.base.Preconditions;

import guru.bonacci.trains.model.Station;
import guru.bonacci.trains.model.homewardbound.HomewardTrain;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class IncomingTrainAtStation extends HomewardTrain {

    public String gotoName;

    
    public IncomingTrainAtStation(HomewardTrain train, Station station) {
    	Preconditions.checkArgument(train.gotoId == station.id, 
    			"This is no good: train.stationId %s and station.id %s", train.gotoId, station.id);
    	
    	this.trainId = train.trainId;
    	this.trainName = train.trainName;
    	this.gotoId = train.gotoId;
    	this.gotoName = station.name;
    	this.togo = train.togo;
    }
}
