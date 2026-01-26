package com.fulfilment.application.monolith.test.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductEndpointUnitTest {

  private static final String PATH = "/product";

  @BeforeEach
  void setup() {
    // Optional: reset database or test data if needed
  }

  @Test
  void testCrudProduct() {
    // List all products, expecting some initial products
    given()
            .when().get(PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(containsString("TONSTAD"))
            .body(containsString("KALLAX"))
            .body(containsString("BESTÅ"));

    // Delete product with ID 1 (TONSTAD)
    given()
            .when().delete(PATH + "/1")
            .then()
            .statusCode(204);

    // List again, TONSTAD should no longer be present
    given()
            .when().get(PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(not(containsString("TONSTAD")))
            .body(containsString("KALLAX"))
            .body(containsString("BESTÅ"));
  }
}
