package com.fulfilment.application.monolith.stores;

import com.fulfilment.application.monolith.legacy.LegacyStoreManagerGateway;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.atLeastOnce;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class StoreResourceIT {

    @InjectMock
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    @Test
    void testCreateStoreTriggersLegacyGateway() {
        // reset to avoid interactions caused by test data insertion at startup
        reset(legacyStoreManagerGateway);

        String payload = "{\"name\":\"TEST-STORE\", \"quantityProductsInStock\": 10}";

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when().post("/stores")
                .then()
                .statusCode(201)
                .body("name", is("TEST-STORE"));

        // verify the gateway was called at least once for the created store
        verify(legacyStoreManagerGateway, atLeastOnce()).createStoreOnLegacySystem(org.mockito.ArgumentMatchers.any(Store.class));
    }
}
