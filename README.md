# testing-redis-kafka-based-apps
Testing Redis and Kafka Based Applications - i2s Insurance Knowledge Time2Share - 04/12/2020

# Required Software

- [Apache Maven](https://maven.apache.org)
- [Java 11](https://jdk.java.net/java-se-ri/11)
- [Docker](https://docs.docker.com/get-docker/)
- [Docker-Compose](https://docs.docker.com/compose/install/)
- [Postman](https://www.postman.com/downloads/)

For running newman tests:
- [Node](https://nodejs.org/en/download/)
- [Newman](https://www.npmjs.com/package/newman) `npm i --global newman`

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `kafka-testcontainers-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/kafka-testcontainers-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/kafka-testcontainers-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

