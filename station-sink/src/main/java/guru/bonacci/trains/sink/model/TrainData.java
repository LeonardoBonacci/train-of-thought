package guru.bonacci.trains.sink.model;

import java.time.Duration;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainData {

    public String trainName;
    public long minutesUntilArrival;

    
    private TrainData(String trainName, long untilArrival) {
        this.trainName = trainName;
        this.minutesUntilArrival = Duration.ofMillis(untilArrival).toMinutes(); 
    }


    public static TrainData from(TrainForAggregation train) {
        return new TrainData(train.trainName, train.msUntilArrival);
    }
}
