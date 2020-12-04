package pt.i2s.time2share.kafkatestcontainers.service;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import pt.i2s.time2share.kafkatestcontainers.model.request.Term;
import pt.i2s.time2share.kafkatestcontainers.model.response.Result;
import pt.i2s.time2share.kafkatestcontainers.service.producer.FibonacciProducerService;
import pt.i2s.time2share.kafkatestcontainers.utils.cache.RedisCache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class FibonacciService {

    private static final String CACHE_KEY_PREFIX = "fibonacci_";

    @Inject
    FibonacciProducerService fibonacciProducerService;

    @Inject
    RedisCache redisCache;

    /**
     * Begin the calculation of the fibonacci term.
     *
     * @param term the object which contains the index to calculate
     * @return void Uni
     */
    public Uni<Void> calculate(Term term) {
        if (!term.getTerm().toString().matches("^-?\\d+$")) {
            return Uni
                    .createFrom()
                    .failure(new IllegalArgumentException("Only integer values allowed!"));
        }

        return redisCache.rxGet(CACHE_KEY_PREFIX + term.getTerm().toString())
                .flatMap(optional -> {
                    if (optional.isPresent()) {
                        return Uni.createFrom().voidItem();
                    }

                    return Uni
                            .createFrom()
                            .completionStage(
                                    () -> CompletableFuture.runAsync(
                                            () -> fibonacciProducerService.produce(
                                                    KafkaRecord.of(
                                                            UUID.randomUUID().toString(),
                                                            Integer.parseInt(term.getTerm().toString())
                                                    )
                                            )
                                    )
                            );
                });
    }

    /**
     * Get the fibonacci term.
     *
     * @param index the index of the term
     * @return Uni with the result if term is already available or failure otherwise
     */
    public Uni<Result> getTerm(final String index) {
        if (!index.matches("^-?\\d+$")) {
            return Uni
                    .createFrom()
                    .failure(new IllegalArgumentException("Only integer values allowed!"));
        }

        return redisCache.rxGet(CACHE_KEY_PREFIX + index)
                .flatMap(optional -> {
                    if (optional.isPresent()) {
                        return Uni.createFrom().item(new Result(optional.get().toString()));
                    }

                    return Uni
                            .createFrom()
                            .failure(new NoSuchElementException(String.format("Term '%s' is not available yet", index)));
                });
    }
}
