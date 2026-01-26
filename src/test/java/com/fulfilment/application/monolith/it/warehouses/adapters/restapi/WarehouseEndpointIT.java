package com.fulfilment.application.monolith.it.warehouses.adapters.restapi;

import com.warehouse.api.beans.Warehouse;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusIntegrationTest
class WarehouseEndpointIT {

    @Test
    void testCreateWarehouse() {
        Warehouse input = new Warehouse();
        input.setId("WH-100");
        input.setLocation("AMSTERDAM");
        input.setCapacity(50);
        input.setStock(5);

        given()
                .contentType("application/json")
                .body(input)
                .when().post("/warehouse")
                .then()
                .statusCode(201)
                .body("id", is("WH-100"))
                .body("location", is("AMSTERDAM"))
                .body("capacity", is(50))
                .body("stock", is(5));
    }

    @Test
    void testGetWarehouseById() {
        given()
                .when().get("/warehouse/WH-100")
                .then()
                .statusCode(200)
                .body("id", is("WH-100"))
                .body("location", is("AMSTERDAM"));
    }

    @Test
    void testListWarehouses() {
        given()
                .when().get("/warehouse")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    void testDeleteWarehouse() {
        given()
                .when().delete("/warehouse/WH-100")
                .then()
                .statusCode(204); // 204 No Content for void delete
    }
}
