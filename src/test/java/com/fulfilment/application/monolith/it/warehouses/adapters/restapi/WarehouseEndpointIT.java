package com.fulfilment.application.monolith.it.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.restapi.dto.Warehouse;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusIntegrationTest
class WarehouseEndpointIT {

    private static Warehouse testWarehouse;

    @BeforeAll
    static void setup() {
        // Set REST-assured base URI/port for Quarkus tests
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8085;
        RestAssured.basePath = "/warehouse";

        // Create a test warehouse for all tests
        testWarehouse = new Warehouse();
        testWarehouse.setBusinessUnitCode("WH-TEST-001");
        testWarehouse.setLocation("AMSTERDAM");
        testWarehouse.setCapacity(50);
        testWarehouse.setStock(5);

        // Insert the warehouse before tests
        testWarehouse = given()
                .contentType("application/json")
                .body(testWarehouse)
                .when().post("/")
                .then()
                .statusCode(201)
                .extract()
                .as(Warehouse.class);
    }

    @Test
    void testCreateWarehouse() {
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setBusinessUnitCode("WH-TEST-002");
        newWarehouse.setLocation("ROTTERDAM");
        newWarehouse.setCapacity(100);
        newWarehouse.setStock(10);

        given()
                .contentType("application/json")
                .body(newWarehouse)
                .when().post("/")
                .then()
                .statusCode(201)
                .body("businessUnitCode", is("WH-TEST-002"))
                .body("location", is("ROTTERDAM"))
                .body("capacity", is(100))
                .body("stock", is(10));
    }

    @Test
    void testGetWarehouseById() {
        given()
                .when().get("/" + testWarehouse.getBusinessUnitCode())
                .then()
                .statusCode(200)
                .body("businessUnitCode", is(testWarehouse.getBusinessUnitCode()))
                .body("location", is(testWarehouse.getLocation()));
    }

    @Test
    void testListWarehouses() {
        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("businessUnitCode", hasItem(testWarehouse.getBusinessUnitCode()));
    }

    @Test
    void testDeleteWarehouse() {
        given()
                .when().delete("/" + testWarehouse.getBusinessUnitCode())
                .then()
                .statusCode(204);

        // Confirm deletion
        given()
                .when().get("/" + testWarehouse.getBusinessUnitCode())
                .then()
                .statusCode(404);
    }
}
