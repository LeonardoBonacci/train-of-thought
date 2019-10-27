package guru.bonacci.trains.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class Station {

    public int id;
    public String name;
}