package guru.bonacci.trains.arrival.streams;

import java.time.Duration;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ArrivalTime {

    private final String trainName;
    private final Duration untilArrival;
    private final Integer fromLoc;
    private final Integer toLoc;
//    
//    public ArrivalTime(String name, Duration until, Integer from, Integer to) {
//        this.trainName = name;
//        this.untilArrival = until;
//        this.fromLoc = from;
//        this.toLoc = to;
//    }
}