package guru.bonacci.trains.model.onmyway;

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
    
    public double lat;
    public double lon;
    
    public int gotoId;
    public long togo;
}
