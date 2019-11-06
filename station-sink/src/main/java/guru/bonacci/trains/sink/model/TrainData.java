package guru.bonacci.trains.sink.model;

import java.time.Duration;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainData {

    public String trainName;
    public long msUntilArrival;

    
    private TrainData(String trainName, long untilArrival) {
        this.trainName = trainName;
        this.msUntilArrival = Duration.ofMillis(untilArrival).toMillis(); 
    }


    public static TrainData from(TrainForAggr train) {
        return new TrainData(train.trainName, train.msUntilArrival);
    }
}
