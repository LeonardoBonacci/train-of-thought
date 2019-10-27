package guru.bonacci.trains.sink.model;

import static java.util.stream.Collectors.toList;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class StationData {

    public int stationId;
    public String stationName;
    public List<TrainData> expected;

    
    private StationData(int stationId, String stationName, List<TrainData> trains) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.expected = trains;
    }

    public static StationData from(StationAggregation aggregation) {
        return new StationData(
                aggregation.stationId,
                aggregation.stationName,
                aggregation.trains.values().stream()
                				.map(TrainData::from)
                				.collect(toList()));
    }
}
