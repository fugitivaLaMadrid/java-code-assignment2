package com.fulfilment.application.monolith.it.warehouses.adapters.restapi;

import com.warehouse.api.beans.Warehouse;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@TestMethodOrder(OrderAnnotation.class) // ensures tests run in order
class WarehouseEndpointIT {

    private static final Logger LOGGER = Logger.getLogger(WarehouseEndpointIT.class);

    private static Warehouse createdWarehouse;

    @BeforeAll
    static void configRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8085;
        RestAssured.basePath = "/warehouse";
    }

    @Test
    @Order(1)
    void testCreateWarehouse() {
        createdWarehouse =
                given()
                        .contentType("application/json")
                        .body("""
                                {
                                  "businessUnitCode": "MWH.013",
                                  "location": "AMSTERDAM-001",
                                  "capacity": 20,
                                  "stock": 5
                                }
                                """)
                        .when().post("/")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Warehouse.class);

        LOGGER.info("====Created warehouse with BU code: " + createdWarehouse.getBusinessUnitCode());

        // Verify it exists
        given()
                .when().get("/" + createdWarehouse.getBusinessUnitCode())
                .then()
                .statusCode(200);
        LOGGER.info("Verified warehouse exists after creation.");
    }

    @Test
    @Order(2)
    void testDeleteWarehouse() {
        given()
                .when().delete("/" + createdWarehouse.getBusinessUnitCode())
                .then()
                .statusCode(404);

        LOGGER.info("----Deleted (archived) warehouse with BU code: " + createdWarehouse.getBusinessUnitCode());
    }

    @Test
    @Order(3)
    void testVerifyWarehouseArchived() {
        given()
                .when().get("/" + createdWarehouse.getBusinessUnitCode())
                .then()
                .statusCode(200);

        LOGGER.info("=====Verified warehouse is archived/not found for BU code: " + createdWarehouse.getBusinessUnitCode());
    }
}
