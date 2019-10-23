package guru.bonacci.trains.history.model;

import lombok.ToString;

@ToString
public class AvgFutureArrival {

    public String name;
    public Double lat;
    public Double lon;
    public long avg;
    public String station;
    
    private int count;
    private long sum;


    public AvgFutureArrival updateFrom(FutureArrival measurement) {
        name = measurement.name;
        lat = measurement.lat;
        lon = measurement.lon;
        station = measurement.station;

        count++;
        sum += measurement.togo;
        avg = sum / count;

        return this;
    }
}