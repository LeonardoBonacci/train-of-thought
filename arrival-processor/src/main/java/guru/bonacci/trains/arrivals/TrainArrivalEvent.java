package guru.bonacci.trains.arrivals;

import java.time.Instant;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TrainArrivalEvent { //extends TrainEvent {

    private final String name;
    private final Instant from;
    private final Instant to;

}