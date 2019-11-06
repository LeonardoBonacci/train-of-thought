package guru.bonacci.trains.sink.model;

import static java.util.stream.Collectors.toList;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class StationData {

    public String stationName;
    public List<TrainData> expected;

    
    private StationData(String stationName, List<TrainData> trains) {
        this.stationName = stationName;
        this.expected = trains;
    }

    public static StationData from(StationAggr aggregation) {
        return new StationData(
                aggregation.stationName,
                aggregation.trains.values().stream()
                				.map(TrainData::from)
                				.collect(toList()));
    }
}
