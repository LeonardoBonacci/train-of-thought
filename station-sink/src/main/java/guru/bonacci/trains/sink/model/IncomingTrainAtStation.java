package guru.bonacci.trains.sink.model;

import com.google.common.base.Preconditions;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class IncomingTrainAtStation extends HomewardTrain {

    public String gotoName;

    
    public IncomingTrainAtStation(HomewardTrain train, Station station) {
    	super(train.id, train.route, train.name, train.togo, train._goto);

    	Preconditions.checkArgument(train._goto == station.id, 
    			"This is no good: train.stationId %d and station.id %d", train._goto, station.id);
    	
    	this.gotoName = station.name;
    }
}
