package guru.bonacci.trains.model.onmyway;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class WayTrain {

    public String trainId;
    public String trainName;
    
    public double lat;
    public double lon;
    
    public int gotoId;
    public String gotoName;
}
