package guru.bonacci.trains.kafkastreams.streams;

import lombok.ToString;

@ToString
public class ArrivalTimeAggr {

    public String busName;
    public Integer fromLoc;
    public Integer toLoc;

    public int count;
    public long sum;
    public long avg;


    public ArrivalTimeAggr updateFrom(ArrivalTime measurement) {
        busName = measurement.busName;
        fromLoc = measurement.fromLoc;
        toLoc = measurement.toLoc;
        
        count++;
        sum += measurement.untilArrival.toMillis();
        avg = sum / count;

        return this;
    }
}