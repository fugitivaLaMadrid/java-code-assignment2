package com.fulfilment.application.monolith.it.legacy;

import com.fulfilment.application.monolith.legacy.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.legacy.LegacyStoreManagerObserver;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.stores.StoreSavedEvent;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;

@QuarkusTest
class LegacyStoreManagerGatewayIT {

    @InjectMock
    LegacyStoreManagerGateway legacyStoreManagerGateway; // Mockito mock

    @Inject
    LegacyStoreManagerObserver legacyStoreManagerObserver; // Real observer

    @Inject
    StoreRepository storeRepository;

    @Test
    @Transactional
    void testCreateStoreTriggersLegacyGateway() {
        // Step 1: Create a store entity manually
        Store store = new Store("TEST-STORE");
        store.setQuantityProductsInStock(10);
        storeRepository.persist(store);
        storeRepository.flush(); // commit so observer can see it

        // Step 2: Fire the observer manually (simulates AFTER_SUCCESS event)
        StoreSavedEvent event = new StoreSavedEvent(store.getId(), StoreSavedEvent.Action.CREATE);
        legacyStoreManagerObserver.onStoreSaved(event);

        // Step 3: Verify the gateway was called
        verify(legacyStoreManagerGateway).createStoreOnLegacySystem(store);
    }
}
