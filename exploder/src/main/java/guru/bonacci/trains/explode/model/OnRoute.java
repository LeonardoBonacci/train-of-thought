package guru.bonacci.trains.explode.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class OnRoute {

    public int ROUTE;
    public int STATION;
}