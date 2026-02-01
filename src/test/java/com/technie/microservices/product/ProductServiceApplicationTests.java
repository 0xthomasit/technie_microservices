package com.technie.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.mongodb.MongoDBContainer;

// TestContainers: library spinning up Docker container
// To write 'Integration' test for 2 Endpoints (Create, ), need 'automated' test (during the build) at well
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Become annotation which dynamically assign MongoDB host + Port by running app => can do that by using @ServiceConnection
class ProductServiceApplicationTests {

    // Initialize MongoDbContainer
    @ServiceConnection
    // (no need to define mongodb URI, Spring Boot will automatically inject the related Url details for our application properties
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.2.4");

    @LocalServerPort
    private Integer port;

    // The configuration to use RestAssured
    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    // Start our MongoDB Test-Containers before running the Test
    static {
        mongoDBContainer.start();
    }

    /* The Integration Test will sping up the application */
    // Setup RestAssure inside the Test
    @Test
    void shouldCreateProduct() {
        String requestBody = """
                {
                    "name": "iPhone 15",
                    "description": "iPhone 15 is a smartphone from Apple",
                    "price": 1000
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/product")
                .then()
                .statusCode(201)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.equalTo("iPhone 15"))
                .body("description", Matchers.equalTo("iPhone 15 is a smartphone from Apple"))
                .body("price", Matchers.equalTo(1000));
    }

}
