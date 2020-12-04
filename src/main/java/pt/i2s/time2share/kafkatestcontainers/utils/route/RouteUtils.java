package pt.i2s.time2share.kafkatestcontainers.utils.route;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pt.i2s.time2share.kafkatestcontainers.model.response.Error;
import pt.i2s.time2share.kafkatestcontainers.model.response.Response;

import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RouteUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteUtils.class);

    private static final Map<Class<? extends Throwable>, Integer> ERROR_CODES = Map.of(
            IllegalArgumentException.class, HttpResponseStatus.BAD_REQUEST.code(),
            NoSuchElementException.class, HttpResponseStatus.NOT_FOUND.code(),
            Exception.class, HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
    );

    /**
     * Return a 200 OK HTTP response.
     *
     * @param ctx  the routing context
     * @param data the data to be sent
     * @param <T>  the fictitious type of data which implements Serializable interface
     */
    public static <T extends Serializable> void okResponse(RoutingContext ctx, T data) {
        ctx
                .response()
                .setStatusCode(HttpResponseStatus.OK.code())
                .putHeader(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .end(
                        JsonObject.mapFrom(
                                new Response<>(data, null)
                        ).encodePrettily()
                );
    }

    /**
     * Return an accepted response
     *
     * @param ctx      the routing context to write the response
     * @param location the location of the resource
     */
    public static void acceptedResponse(RoutingContext ctx, String location) {
        ctx
                .response()
                .setStatusCode(HttpResponseStatus.ACCEPTED.code())
                .putHeader(HttpHeaderNames.LOCATION.toString(), location)
                .end();
    }

    /**
     * Return an error response based on a throwable
     *
     * @param ctx   the routing context to write the response
     * @param error the error thrown
     */
    public static void errorResponse(RoutingContext ctx, Throwable error) {
        final int code = Optional.ofNullable(ERROR_CODES.get(error.getClass())).orElse(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

        ctx.response()
                .setStatusCode(code)
                .putHeader(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .end(
                        JsonObject.mapFrom(
                                new Response<>(null, new Error(code, error.getMessage()))
                        ).encodePrettily()
                );
    }
}
