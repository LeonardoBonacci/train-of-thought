package guru.bonacci.trains.predict.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class SoLongTrain {

    public int route;
    public String name;
    public double lat;
    public double lon;
    public long avg;
    public int station;
}
