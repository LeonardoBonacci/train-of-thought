package org.acme.quarkus.sample.kafkastreams.model;

import java.time.Duration;

import io.quarkus.runtime.annotations.RegisterForReflection;


@RegisterForReflection
public class TrainData {

    public String trainName;
    public long minutesUntilArrival;

    
    private TrainData(String trainName, long untilArrival) {
        this.trainName = trainName;
        this.minutesUntilArrival = Duration.ofMillis(untilArrival).toMinutes(); 
    }


    public static TrainData from(Train train) {
        return new TrainData(train.trainName, train.msUntilArrival);
    }
}
