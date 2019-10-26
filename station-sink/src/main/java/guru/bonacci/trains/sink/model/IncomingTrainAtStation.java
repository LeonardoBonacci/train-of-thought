package guru.bonacci.trains.sink.model;

import com.google.common.base.Preconditions;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class IncomingTrainAtStation {

    public String trainId;
    public String trainName;
    
    public int stationId;
    public String stationName;

    public long togo;
    
    
    public IncomingTrainAtStation(IncomingTrain train, Station station) {
    	Preconditions.checkArgument(train.gotoId != station.id, "This is no good");
    	
    	this.trainId = train.trainId;
    	this.trainName = train.trainName;
    	this.stationId = train.gotoId;
    	this.stationName = station.name;
    	this.togo = train.togo;
    }
}
