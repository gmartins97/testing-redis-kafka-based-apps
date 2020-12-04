package pt.i2s.time2share.kafkatestcontainers.route;

import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pt.i2s.time2share.kafkatestcontainers.model.request.Term;
import pt.i2s.time2share.kafkatestcontainers.model.response.Response;
import pt.i2s.time2share.kafkatestcontainers.service.FibonacciService;
import pt.i2s.time2share.kafkatestcontainers.utils.route.RouteUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import static pt.i2s.time2share.kafkatestcontainers.route.FibonacciReactiveRoute.PATH;

@ApplicationScoped
@RouteBase(path = PATH, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Tag(name = "Fibonacci", description = "Endpoints related with fibonacci sequence")
public class FibonacciReactiveRoute {

    public static final String PATH = "/api/v1/fibonacci";

    @Inject
    FibonacciService service;

    // Open API
    @Operation(
            operationId = "calculateTerm",
            summary = "Calculate fibonacci term"
    )
    @APIResponse(
            responseCode = "202",
            name = "Success",
            description = "Accepted"
    )
    @APIResponse(
            responseCode = "400",
            name = "badRequest",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = Response.class))
    )
    @APIResponse(
            responseCode = "500",
            name = "error",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = Response.class))
    )
    @Route(path = "/", methods = HttpMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public void calculateTerm(
            @RequestBody(
                    description = "Term request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Term.class))
            ) @Body Term term,
            RoutingContext ctx
    ) {
        service
                .calculate(term)
                .subscribe()
                .with(
                        ignored -> RouteUtils.acceptedResponse(ctx, ctx.request().host() + PATH + "/" + term.getTerm().toString()),
                        error -> RouteUtils.errorResponse(ctx, error)
                );
    }

    // Open API
    @Operation(
            operationId = "getTerm",
            summary = "Get fibonacci term"
    )
    @APIResponse(
            responseCode = "200",
            name = "success",
            description = "Ok",
            content = @Content(schema = @Schema(implementation = Response.class))
    )
    @APIResponse(
            responseCode = "500",
            name = "error",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = Response.class))
    )
    @APIResponse(
            responseCode = "400",
            name = "badRequest",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = Response.class))
    )
    @APIResponse(
            responseCode = "404",
            name = "notFound",
            description = "Not Found",
            content = @Content(schema = @Schema(implementation = Response.class))
    )
    @Route(path = "/:term", methods = HttpMethod.GET, produces = MediaType.APPLICATION_JSON)
    public void term(@Param("term") final String index, RoutingContext ctx) {
        service
                .getTerm(index)
                .subscribe()
                .with(
                        result -> RouteUtils.okResponse(ctx, result),
                        error -> RouteUtils.errorResponse(ctx, error)
                );
    }
}
