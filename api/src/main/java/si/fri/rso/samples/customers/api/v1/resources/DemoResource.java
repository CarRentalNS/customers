package si.fri.rso.samples.customers.api.v1.resources;

import com.kumuluz.ee.common.runtime.EeRuntime;
import com.kumuluz.ee.logs.cdi.Log;
import si.fri.rso.samples.customers.api.v1.dtos.HealthDto;
import si.fri.rso.samples.customers.api.v1.dtos.LoadDto;
import si.fri.rso.samples.customers.services.configuration.AppProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("demo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Log
public class DemoResource {

    private Logger log = Logger.getLogger(DemoResource.class.getName());

    @Inject
    private AppProperties appProperties;

    @GET
    @Path("instanceid")
    public Response getInstanceId() {

        String instanceId =
                "{\"instanceId\" : \"" + EeRuntime.getInstance().getInstanceId() + "\"}";

        return Response.ok(instanceId).build();
    }

    @POST
    @Path("healthy")
    public Response setHealth(HealthDto health) {
        appProperties.setHealthy(health.getHealthy());
        log.info("Setting health to " + health.getHealthy());
        return Response.ok().build();
    }

    @POST
    @Path("load")
    public Response loadOrder(LoadDto loadDto) {

        for (int i = 1; i <= loadDto.getN(); i++) {
            fibonacci(i);
        }

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("info")
    public Response info() {

        JsonObject json = Json.createObjectBuilder()
                .add("clani", Json.createArrayBuilder().add("jm1234"))
                .add("opis_projekta", "Nas projekt implementira aplikacijo za upravljanje naročil.")
                .add("mikrostoritve", Json.createArrayBuilder().add("http://35.204.91.158:8081/v1/orders"))
                .add("github", Json.createArrayBuilder().add("https://github.com/jmezna/rso-customers"))
                .add("travis", Json.createArrayBuilder().add("https://travis-ci.org/jmezna/rso-customers"))
                .add("dockerhub", Json.createArrayBuilder().add("https://hub.docker.com/r/jmezna/rso-customers"))
                .build();


        return Response.ok(json.toString()).build();
    }

    private long fibonacci(int n) {
        if (n <= 1) return n;
        else return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
