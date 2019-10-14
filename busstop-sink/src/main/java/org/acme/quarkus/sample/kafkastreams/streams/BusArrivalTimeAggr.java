package org.acme.quarkus.sample.kafkastreams.streams;

import lombok.ToString;

@ToString
public class BusArrivalTimeAggr {

    public String busName;
    public Integer fromLoc;
    public Integer toLoc;

    public int count;
    public long sum;
    public long avg;


    public BusArrivalTimeAggr updateFrom(BusArrivalTime measurement) {
        busName = measurement.busName;
        fromLoc = measurement.fromLoc;
        toLoc = measurement.toLoc;
        
        count++;
        sum += measurement.untilArrival.toMillis();
        avg = sum / count;

        return this;
    }
}