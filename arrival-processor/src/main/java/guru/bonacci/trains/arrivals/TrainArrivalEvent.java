package guru.bonacci.trains.arrivals;

import java.time.Duration;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TrainArrivalEvent { //extends TrainEvent {

    private final String name;
    private final Duration untilArrival;
}