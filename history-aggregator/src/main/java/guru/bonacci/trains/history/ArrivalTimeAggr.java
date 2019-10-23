package guru.bonacci.trains.history;

import lombok.ToString;

@ToString
public class ArrivalTimeAggr {

    public String name;
    public Double fromLoc;
    public Double toLoc;

    public int count;
    public long sum;
    public long avg;


    public ArrivalTimeAggr updateFrom(TrainArrivalEvent measurement) {
        name = measurement.name;
//        fromLoc = measurement.fromLoc;
//        toLoc = measurement.toLoc;
//        
//        count++;
//        sum += measurement.untilArrival.toMillis();
//        avg = sum / count;

        return this;
    }
}