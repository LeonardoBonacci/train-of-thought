package org.acme.quarkus.sample.kafkastreams.model;

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
    	Preconditions.checkArgument(train.togo != station.id, "How can this be?");
    	
    	this.trainId = train.trainId;
    	this.trainName = train.trainName;
    	this.stationId = train._goto;
    	this.stationName = station.name;
    	this.togo = train.togo;
    }
}
