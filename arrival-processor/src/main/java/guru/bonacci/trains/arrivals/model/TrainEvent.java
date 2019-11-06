package guru.bonacci.trains.arrivals.model;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainEvent {

    public String id;
    public int route;
    public String name;
    public Instant moment;
    public double lat;
    public double lon;
}