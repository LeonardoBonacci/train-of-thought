package guru.bonacci.trains.predict.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class WayTrain {

    public String id;
    public int route;
    public String name;
    public double lat;
    public double lon;
    public int _goto;
}
