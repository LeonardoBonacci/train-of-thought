package guru.bonacci.trains.arrivals;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class TrainEvent {

    public String id;
    public String name;
    public Instant moment;
    public Double lat;
    public Double lon;
}