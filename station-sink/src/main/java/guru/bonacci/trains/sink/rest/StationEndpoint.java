package guru.bonacci.trains.sink.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import guru.bonacci.trains.sink.streams.InteractiveQueries;
import guru.bonacci.trains.sink.streams.PipelineMetadata;
import guru.bonacci.trains.sink.streams.StationDataResult;

@ApplicationScoped
@Path("/train-stations")
public class StationEndpoint {

    @Inject
    InteractiveQueries interactiveQueries;

    @GET
    @Path("/data/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStationData(@PathParam("id") int id) {
        StationDataResult result = interactiveQueries.getStationData(id);

        if (result.getResult().isPresent()) {
            return Response.ok(result.getResult().get()).build();
        }
        else if (result.getHost().isPresent()) {
            URI otherUri = getOtherUri(result.getHost().get(), result.getPort().getAsInt(), id);
            return Response.seeOther(otherUri).build();
        }
        else {
            return Response.status(Status.NOT_FOUND.getStatusCode(), "No data found for train station " + id).build();
        }
    }

    @GET
    @Path("/meta-data")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PipelineMetadata> getMetaData() {
        return interactiveQueries.getMetaData();
    }

    private URI getOtherUri(String host, int port, int id) {
        try {
            return new URI("http://" + host + ":" + port + "/train-stations/data/" + id);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
