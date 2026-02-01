package com.fulfilment.application.monolith.it.stores;

import com.fulfilment.application.monolith.legacy.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.reset;

@QuarkusTest
class StoreResourceIT {

    private static final String STORES_ID = "/stores/{id}";

    @Inject
    StoreRepository storeRepository;

    @InjectMock
    LegacyStoreManagerGateway legacyStoreManagerGateway; // Mock so observer does nothing

    @BeforeEach
    @Transactional
    void setup() {
        // Reset mocks to ensure clean state
        reset(legacyStoreManagerGateway);

        // Clear all previous rows
        storeRepository.deleteAll();
        storeRepository.flush();

        // Insert fresh test data
        Store store1 = new Store("Berlin Store");
        store1.setQuantityProductsInStock(10);
        storeRepository.persist(store1);

        Store store2 = new Store("Amsterdam Store");
        store2.setQuantityProductsInStock(20);
        storeRepository.persist(store2);

        storeRepository.flush(); // make sure REST endpoints see the new rows
    }

    // ---------- GET ALL ----------
    @Test
    void getAllStores_sortedByName() {
        given()
                .when().get("/stores")
                .then()
                .statusCode(200)
                .body("$.size()", is(2))
                .body("name", contains(
                        "Amsterdam Store",
                        "Berlin Store"
                ));
    }

    // ---------- GET ONE ----------
    @Test
    void getSingleStore() {
        Store store = storeRepository.findAll().firstResult();

        given()
                .when().get(STORES_ID, store.getId())
                .then()
                .statusCode(200)
                .body("id", is(store.getId().intValue()))
                .body("name", is(store.getName()))
                .body("quantityProductsInStock", is(store.getQuantityProductsInStock()));
    }

    @Test
    void getSingleStore_notFound() {
        // Using an ID that is guaranteed missing
        long missingId = storeRepository.findAll().stream().mapToLong(Store::getId).max().orElse(0) + 1000;

        given()
                .when().get(STORES_ID, missingId)
                .then()
                .statusCode(404)
                .body(containsString("does not exist"));
    }

    // ---------- CREATE ----------
    @Test
    void createStore() {
        String payload = """
                {
                  "name": "Paris Store",
                  "quantityProductsInStock": 15
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/stores")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("Paris Store"))
                .body("quantityProductsInStock", is(15));
    }

    @Test
    void createStore_withId_isIgnored_andStoreIsCreated() {
        String payload = """
                {
                  "id": 1,
                  "name": "Ignored Id Store"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/stores")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("Ignored Id Store"));
    }

    // ---------- UPDATE ----------
    @Test
    void updateStore() {
        Store existing = storeRepository.findAll().firstResult();

        String payload = """
                {
                  "name": "Updated Store",
                  "quantityProductsInStock": 99
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().put("/stores/{id}", existing.getId())
                .then()
                .statusCode(200)
                .body("name", is("Updated Store"))
                .body("quantityProductsInStock", is(99));
    }

    @Test
    void updateStore_missingName_shouldFail() {
        Store existing = storeRepository.findAll().firstResult();

        String payload = """
                {
                  "quantityProductsInStock": 5
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().put(STORES_ID, existing.getId())
                .then()
                .statusCode(422);
    }

    // ---------- PATCH ----------
    @Test
    void patchStore_quantityOnly() {
        Store existing = storeRepository.findAll().firstResult();

        String payload = """
                {
                  "quantityProductsInStock": 50
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().patch(STORES_ID, existing.getId())
                .then()
                .statusCode(200)
                .body("quantityProductsInStock", is(50))
                .body("name", is(existing.getName()));
    }

    @Test
    void patchStore_nameOnly() {
        Store existing = storeRepository.findAll().firstResult();

        String payload = """
                {
                  "name": "Renamed Store"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().patch(STORES_ID, existing.getId())
                .then()
                .statusCode(200)
                .body("name", is("Renamed Store"));
    }

    // ---------- DELETE ----------
    @Test
    void deleteStore() {
        Store existing = storeRepository.findAll().firstResult();

        given()
                .when().delete(STORES_ID, existing.getId())
                .then()
                .statusCode(204);

        given()
                .when().get(STORES_ID, existing.getId())
                .then()
                .statusCode(404);
    }
}
