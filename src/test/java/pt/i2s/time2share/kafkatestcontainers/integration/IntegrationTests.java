package pt.i2s.time2share.kafkatestcontainers.integration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(TestResources.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTests {

    private static final String ENDPOINT = "/api/v1/fibonacci/";

    private static final Headers HEADERS = Headers.headers(
            new Header("content-type", MediaType.APPLICATION_JSON),
            new Header("accept", MediaType.APPLICATION_JSON)
    );

    @Test
    @Order(1)
    @DisplayName("Demand calculation of a fibonacci term")
    void testCalculateFibonacciTerm() {
        RestAssured
                .given()
                .headers(HEADERS)
                .body(new JsonObject().put("term", "28").encode())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(202)
                .header("Location", containsString(ENDPOINT + 28));
    }

    @Test
    @Order(2)
    @DisplayName("Get fibonacci term")
    void testGetFibonacciTerm() {

        // get the previous term
        Awaitility
                .await()
                .atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> {
                    final Response response =
                            RestAssured
                                    .given()
                                    .headers(HEADERS)
                                    .pathParam("idx", 28)
                                    .when()
                                    .get(ENDPOINT + "{idx}")
                                    .thenReturn();

                    final JsonObject responseBody = new JsonObject(response.body().asString());

                    /*assertAll(
                            () -> assertEquals(200, response.statusCode()),
                            () -> assertNotNull(responseBody.getJsonObject("data")),
                            () -> assertNull(responseBody.getJsonObject("error")),
                            () -> assertEquals("317811", responseBody.getJsonObject("data").getString("result"))
                    );*/

                    return response.statusCode() == 200
                            && responseBody.getJsonObject("data") != null
                            && responseBody.getJsonObject("error") == null
                            && "317811".equals(responseBody.getJsonObject("data").getString("result"));
                });

        // get a lower term
        Awaitility
                .await()
                .atMost(150, TimeUnit.MILLISECONDS)
                .until(() -> {
                    final Response response =
                            RestAssured
                                    .given()
                                    .headers(HEADERS)
                                    .pathParam("idx", 10)
                                    .when()
                                    .get(ENDPOINT + "{idx}")
                                    .thenReturn();

                    final JsonObject responseBody = new JsonObject(response.body().asString());

                    /*assertAll(
                            () -> assertEquals(200, response.statusCode()),
                            () -> assertNotNull(responseBody.getJsonObject("data")),
                            () -> assertNull(responseBody.getJsonObject("error")),
                            () -> assertEquals("55", responseBody.getJsonObject("data").getString("result"))
                    );*/

                    return response.statusCode() == 200
                            && responseBody.getJsonObject("data") != null
                            && responseBody.getJsonObject("error") == null
                            && "55".equals(responseBody.getJsonObject("data").getString("result"));
                });
    }

    @Test
    @Order(3)
    @DisplayName("Demand calculation of invalid fibonacci term")
    void testCalculateInvalidFibonacciTerm() {
        RestAssured
                .given()
                .headers(HEADERS)
                .body(new JsonObject().put("term", -1).encode())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(202)
                .header("Location", containsString(ENDPOINT + "-1"));
    }

    @Order(4)
    @DisplayName("Get unavailable fibonacci terms (one invalid and one not calculated yet)")
    @ParameterizedTest
    @ValueSource(ints = {-1, 29})
    void testGetInvalidFibonacciTerm(int idx) {
        final JsonObject responseBody = new JsonObject(
                RestAssured
                        .given()
                        .headers(HEADERS)
                        .pathParam("idx", idx)
                        .when()
                        .get(ENDPOINT + "{idx}")
                        .then()
                        .statusCode(404)
                        .extract()
                        .body()
                        .asString()
        );

        assertAll(
                () -> assertNull(responseBody.getJsonObject("data")),
                () -> assertNotNull(responseBody.getJsonObject("error")),
                () -> assertEquals(404, responseBody.getJsonObject("error").getInteger("code")),
                () -> assertEquals(String.format("Term '%d' is not available yet", idx), responseBody.getJsonObject("error").getString("message"))
        );
    }

    @Test
    @Order(5)
    @DisplayName("Demand calculation of previously calculated fibonacci term")
    void testCalculatePreviouslyCalculatedFibonacciTerm() {
        RestAssured
                .given()
                .headers(HEADERS)
                .body(new JsonObject().put("term", 28).encode())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(202)
                .header("Location", containsString(ENDPOINT + "28"));
    }

    @Test
    @Order(6)
    @DisplayName("Demand calculation of invalid fibonacci term (not integer)")
    void testCalculateNotIntegerFibonacciTerm() {
        RestAssured
                .given()
                .headers(HEADERS)
                .body(new JsonObject().put("term", "").encode())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body(containsString("Only integer values allowed!"));
    }

    @Test
    @Order(7)
    @DisplayName("Get invalid fibonacci term (not integer)")
    void testGetNotIntegerFibonacciTerm() {
        RestAssured
                .given()
                .pathParam("idx", "abc")
                .headers(HEADERS)
                .when()
                .get(ENDPOINT + "{idx}")
                .then()
                .statusCode(400)
                .body(containsString("Only integer values allowed!"));
    }
}
