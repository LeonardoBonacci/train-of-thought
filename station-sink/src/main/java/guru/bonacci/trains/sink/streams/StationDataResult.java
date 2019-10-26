package guru.bonacci.trains.sink.streams;

import java.util.Optional;
import java.util.OptionalInt;

import guru.bonacci.trains.sink.model.StationData;

public class StationDataResult {

    private static StationDataResult NOT_FOUND = new StationDataResult(null, null, null);

    private final StationData result;
    private final String host;
    private final Integer port;

    private StationDataResult(StationData result, String host, Integer port) {
        this.result = result;
        this.host = host;
        this.port = port;
    }

    public static StationDataResult found(StationData data) {
        return new StationDataResult(data, null, null);
    }

    public static StationDataResult foundRemotely(String host, int port) {
        return new StationDataResult(null, host, port);
    }

    public static StationDataResult notFound() {
        return NOT_FOUND;
    }

    public Optional<StationData> getResult() {
        return Optional.ofNullable(result);
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public OptionalInt getPort() {
        return port != null ? OptionalInt.of(port) : OptionalInt.empty();
    }
}
