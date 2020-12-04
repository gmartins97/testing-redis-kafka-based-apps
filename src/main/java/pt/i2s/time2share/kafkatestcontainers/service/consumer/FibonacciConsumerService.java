package pt.i2s.time2share.kafkatestcontainers.service.consumer;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mutiny.core.Vertx;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import pt.i2s.time2share.kafkatestcontainers.utils.cache.RedisCache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class FibonacciConsumerService {

    private static final String INCOMING_CHANNEL = "fib_in";
    private static final String CACHE_KEY_PREFIX = "fibonacci_";

    private static final Logger LOGGER = LoggerFactory.getLogger(FibonacciConsumerService.class);

    @Inject
    Vertx vertx;

    @Inject
    RedisCache cache;

    /**
     * Consume the messages from Kafka topic.
     *
     * @param record the message containing the term to calculate
     * @return {@link IncomingKafkaRecord#ack()} or {@link IncomingKafkaRecord#nack(Throwable)}
     */
    @Incoming(INCOMING_CHANNEL)
    public CompletionStage<Void> consume(IncomingKafkaRecord<String, Integer> record) {
        // we use executeBlocking() if the code we want to run is blocking because vert.x throws exception
        // when a thread is blocked for more than 2 seconds
        return vertx.executeBlocking(call -> {
            // Get event key from record
            final String key = record.getKey();

            LOGGER.info("Consumed event w/ key " + key + " & content: " + record.getPayload());

            long start = System.currentTimeMillis();

            // recursive fibonacci ( O(2^n) )
            fibonacci(record.getPayload())
                    .subscribe()
                    .with(res -> {
                        long end = System.currentTimeMillis();

                        LOGGER.info("Success consuming event w/ key " + key + " in " + (end - start) + " ms");

                        call.complete(null);
                    }, error -> {
                        LOGGER.error("Error consuming event w/ key " + key + ", Reason: " + error.getLocalizedMessage());

                        call.complete(error);
                    });
        }).subscribeAsCompletionStage()
                .thenCompose(res -> {
                    if (res instanceof Throwable) {
                        return record.nack((Throwable) res);
                    }

                    return record.ack();
                });
    }

    /**
     * Calculate the fibonacci term.
     *
     * @param index the index of the term
     */
    private Uni<Void> fibonacci(int index) {
        return fibonacciRecursive(index)
                .flatMap(res -> Uni.createFrom().voidItem());
    }

    /**
     * Fibonacci recursive calculation.
     *
     * @param index the index of the term
     * @return the correspondent term
     */
    private Uni<BigInteger> fibonacciRecursive(int index) {
        if (index < 0) {
            return Uni.createFrom().failure(new IllegalArgumentException("Only terms greater or equal to zero!"));
        }

        if (index < 2) {
            return cache.rxGet(CACHE_KEY_PREFIX + index)
                    .flatMap(response -> {
                        if (response.isPresent()) {
                            return Uni.createFrom().item(new BigInteger(response.get().toString()));
                        }

                        return cache.rxSet(CACHE_KEY_PREFIX + index, String.valueOf(index))
                                .flatMap(res -> Uni.createFrom().item(BigInteger.valueOf(index)));
                    });
        }

        return cache.rxGet(CACHE_KEY_PREFIX + index)
                .flatMap(response -> {
                    if (response.isPresent()) {
                        return Uni.createFrom().item(new BigInteger(response.get().toString()));
                    }

                    return fibonacciRecursive(index - 1)
                            .flatMap(res -> fibonacciRecursive(index - 2)
                                    .flatMap(res2 -> cache.rxSet(CACHE_KEY_PREFIX + index, res.add(res2).toString())
                                            .flatMap(ignored -> Uni.createFrom().item(res.add(res2))))
                            );
                });
    }
}
