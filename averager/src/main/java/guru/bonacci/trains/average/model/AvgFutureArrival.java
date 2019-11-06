package guru.bonacci.trains.average.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.ToString;

@ToString
public class AvgFutureArrival {

    public int route;
    public String name;
    public double lat;
    public double lon;
    public long avg;
    public int station;

    @JsonIgnore
    private int count;
    @JsonIgnore
    private long sum;


    public AvgFutureArrival updateFrom(FutureArrival measurement) {
    	route = measurement.route;
        name = measurement.name;
        lat = measurement.lat;
        lon = measurement.lon;
        station = measurement._goto;

        count++;
        sum += measurement.togo;
        avg = sum / count;

        return this;
    }
}