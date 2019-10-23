package guru.bonacci.trains.arrivals.model;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class ArrivalAt {

    public String ID;
    public Instant moment;
    public String station;
}