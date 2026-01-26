package com.fulfilment.application.monolith.it.legacy;

import com.fulfilment.application.monolith.legacy.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.stores.Store;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@QuarkusTest
class LegacyStoreManagerGatewayIT {

    @InjectMock
    LegacyStoreManagerGateway legacyStoreManagerGateway; // Quarkus will now inject a Mockito mock

    @Test
    void testCreateStoreTriggersLegacyGateway() {
        reset(legacyStoreManagerGateway);

        String payload = "{\"name\":\"TEST-STORE\", \"quantityProductsInStock\": 10}";

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when().post("/stores")
                .then()
                .statusCode(201);

        verify(legacyStoreManagerGateway, atLeastOnce())
                .createStoreOnLegacySystem(any(Store.class));
    }
}
