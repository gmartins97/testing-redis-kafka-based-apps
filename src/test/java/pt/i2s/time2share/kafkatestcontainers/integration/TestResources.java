package pt.i2s.time2share.kafkatestcontainers.integration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.stream.Stream;

public class TestResources implements QuarkusTestResourceLifecycleManager {

    private static final Network NETWORK = Network.newNetwork();
    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:6.0.7"))
            .withExposedPorts(6379)
            .withCommand("redis-server")
            .withNetwork(NETWORK);
    private static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.2.1"))
            .withEmbeddedZookeeper()
            .withNetwork(NETWORK);

    @Override
    public Map<String, String> start() {
        Startables.deepStart(
                Stream.of(
                        REDIS,
                        KAFKA
                )
        ).join();


        return Map.of(
                "quarkus.redis.hosts", "redis://" + REDIS.getContainerIpAddress() + ":" + REDIS.getFirstMappedPort(),
                "kafka.bootstrap.servers", KAFKA.getBootstrapServers()
        );
    }

    @Override
    public void stop() {
        REDIS.stop();
        KAFKA.stop();
    }
}
