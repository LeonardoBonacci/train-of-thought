package guru.bonacci.trains.history.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class SoLongTrain {

    public String trainName;
    public String geoHash;
    public int gotoId;
    public long togo;
}
