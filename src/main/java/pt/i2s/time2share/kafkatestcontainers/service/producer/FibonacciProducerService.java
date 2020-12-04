package pt.i2s.time2share.kafkatestcontainers.service.producer;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FibonacciProducerService {

    public static final String OUTGOING_CHANNEL = "fib_out";

    private FlowableEmitter<KafkaRecord<String, Integer>> emitter;
    private Flowable<KafkaRecord<String, Integer>> outgoingStream;

    @PostConstruct
    void setUp() {
        outgoingStream = Flowable.create(theEmitter -> this.emitter = theEmitter, BackpressureStrategy.BUFFER);
    }

    @PreDestroy
    void tearDown() {
        emitter.onComplete();
    }

    @Outgoing(OUTGOING_CHANNEL)
    Publisher<KafkaRecord<String, Integer>> produce() {
        return outgoingStream;
    }

    /**
     * Produce messages to kafka topic.
     *
     * @param message the message containing the term to be calculated
     */
    public void produce(KafkaRecord<String, Integer> message) {
        emitter.onNext(message);
    }
}
