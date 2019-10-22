package guru.bonacci.trains.arrivals;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class ArrivedAt {

    public String ID;
    public Instant moment;
    public String station;
}