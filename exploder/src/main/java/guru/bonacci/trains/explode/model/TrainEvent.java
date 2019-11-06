package guru.bonacci.trains.explode.model;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class TrainEvent {

    public String id;
    public Integer route;
    public String name;
    public Instant moment;
    public Double lat;
    public Double lon;
}