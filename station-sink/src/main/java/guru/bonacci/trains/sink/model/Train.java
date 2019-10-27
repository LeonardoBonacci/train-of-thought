package guru.bonacci.trains.sink.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class Train {

    public String trainName;
    public long msUntilArrival;
}
