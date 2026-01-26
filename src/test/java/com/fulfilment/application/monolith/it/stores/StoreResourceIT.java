package com.fulfilment.application.monolith.it.stores;

import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class StoreResourceIT {

    @Inject
    StoreRepository storeRepository;

    @BeforeEach
    @Transactional
    void setup() {
        storeRepository.deleteAll();

        Store store1 = new Store("Berlin Store");
        store1.setQuantityProductsInStock(10);

        Store store2 = new Store("Amsterdam Store");
        store2.setQuantityProductsInStock(20);

        storeRepository.persist(store1);
        storeRepository.persist(store2);
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
                .when().get("/stores/{id}", store.getId())
                .then()
                .statusCode(200)
                .body("id", is(store.getId().intValue()))
                .body("name", is(store.getName()))
                .body("quantityProductsInStock", is(store.getQuantityProductsInStock()));
    }

    @Test
    void getSingleStore_notFound() {
        given()
                .when().get("/stores/{id}", 9999)
                .then()
                .statusCode(404)
                .body(containsString("does not exist"));
    }

    // ---------- CREATE ----------
    @Test
    void createStore() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "name": "Paris Store",
                  "quantityProductsInStock": 15
                }
                """)
                .when().post("/stores")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("Paris Store"))
                .body("quantityProductsInStock", is(15));
    }

    @Test
    void createStore_withId_isIgnored_andStoreIsCreated() {
        given()
                .contentType(ContentType.JSON)
                .body("""
            {
              "id": 1,
              "name": "Ignored Id Store"
            }
            """)
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

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "name": "Updated Store",
                  "quantityProductsInStock": 99
                }
                """)
                .when().put("/stores/{id}", existing.getId())
                .then()
                .statusCode(200)
                .body("name", is("Updated Store"))
                .body("quantityProductsInStock", is(99));
    }

    @Test
    void updateStore_missingName_shouldFail() {
        Store existing = storeRepository.findAll().firstResult();

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "quantityProductsInStock": 5
                }
                """)
                .when().put("/stores/{id}", existing.getId())
                .then()
                .statusCode(422);
    }

    // ---------- PATCH ----------
    @Test
    void patchStore_quantityOnly() {
        Store existing = storeRepository.findAll().firstResult();

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "quantityProductsInStock": 50
                }
                """)
                .when().patch("/stores/{id}", existing.getId())
                .then()
                .statusCode(200)
                .body("quantityProductsInStock", is(50))
                .body("name", is(existing.getName()));
    }

    @Test
    void patchStore_nameOnly() {
        Store existing = storeRepository.findAll().firstResult();

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "name": "Renamed Store"
                }
                """)
                .when().patch("/stores/{id}", existing.getId())
                .then()
                .statusCode(200)
                .body("name", is("Renamed Store"));
    }

    // ---------- DELETE ----------
    @Test
    void deleteStore() {
        Store existing = storeRepository.findAll().firstResult();

        given()
                .when().delete("/stores/{id}", existing.getId())
                .then()
                .statusCode(204);

        given()
                .when().get("/stores/{id}", existing.getId())
                .then()
                .statusCode(404);
    }
}
