package pt.i2s.time2share.kafkatestcontainers.utils.cache;

import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mutiny.redis.client.Response;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Redis Client wrapper.
 */
@ApplicationScoped
public class RedisCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

    /**
     * The reactive redis client instance.
     */
    @Inject
    ReactiveRedisClient reactiveRedisClient;

    /**
     * Get a value from the redis cache.
     *
     * @param key the key to get the value
     * @return A {@link Uni<Optional<Response>>}
     */
    public Uni<Optional<Response>> rxGet(String key) {
        LOGGER.info(String.format("REDIS: Getting key '%s'", key));
        return reactiveRedisClient
                .get(key)
                .flatMap(response -> Uni.createFrom().item(Optional.ofNullable(response)));
    }

    /**
     * Set a value in redis cache.
     *
     * @param key   the key
     * @param value the value to be set
     * @return A {@link Uni<Void>} indicating operation's success or failure
     */
    public Uni<Void> rxSet(String key, String value) {
        LOGGER.info(String.format("REDIS: Setting key '%s' and value '%s'", key, value));
        return reactiveRedisClient
                .set(List.of(key, value))
                .flatMap(response -> Uni.createFrom().voidItem());
    }
}
