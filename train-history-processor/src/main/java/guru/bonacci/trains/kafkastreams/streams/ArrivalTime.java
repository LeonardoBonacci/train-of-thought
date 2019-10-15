package guru.bonacci.trains.kafkastreams.streams;

import java.time.Duration;

import lombok.ToString;

@ToString
public class ArrivalTime {

    public String busName;
    public Duration untilArrival;
    public Integer fromLoc;
    public Integer toLoc;
    
    public ArrivalTime(String name, Duration until, Integer from, Integer to) {
        this.busName = name;
        this.untilArrival = until;
        this.fromLoc = from;
        this.toLoc = to;
    }
}