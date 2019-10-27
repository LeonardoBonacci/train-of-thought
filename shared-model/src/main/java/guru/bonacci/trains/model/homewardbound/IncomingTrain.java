package guru.bonacci.trains.model.homewardbound;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@RegisterForReflection
public class IncomingTrain {

    public String trainId;
    public String trainName;
    
    public int gotoId;
    public long togo;
}
