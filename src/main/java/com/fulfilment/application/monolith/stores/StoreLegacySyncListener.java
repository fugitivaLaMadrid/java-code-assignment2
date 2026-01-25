package com.fulfilment.application.monolith.stores;

import com.fulfilment.application.monolith.legacy.LegacyStoreManagerGateway;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StoreLegacySyncListener {

    private static final Logger LOGGER = Logger.getLogger(StoreLegacySyncListener.class);

    private final StoreRepository storeRepository;
    private final LegacyStoreManagerGateway legacyStoreManagerGateway;

    // ---------- Constructor injection ----------
    public StoreLegacySyncListener(StoreRepository storeRepository,
                                   LegacyStoreManagerGateway legacyStoreManagerGateway) {
        this.storeRepository = storeRepository;
        this.legacyStoreManagerGateway = legacyStoreManagerGateway;
    }

    @SuppressWarnings("squid:S2325") // intentional: repository access
    public void onStoreSaved(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreSavedEvent event) {
        if (event == null || event.getStoreId() == null) {
            LOGGER.warn("StoreSavedEvent or storeId is null, skipping legacy sync");
            return;
        }

        Long storeId = event.getStoreId();

        try {
            storeRepository.findByIdOptional(storeId).ifPresentOrElse(
                    store -> {
                        switch (event.getAction()) {
                            case CREATE -> legacyStoreManagerGateway.createStoreOnLegacySystem(store);
                            case UPDATE -> legacyStoreManagerGateway.updateStoreOnLegacySystem(store);
                            default -> legacyStoreManagerGateway.updateStoreOnLegacySystem(store);
                        }
                    },
                    () -> LOGGER.warnf("Store with id %d not found when trying to sync to legacy system", storeId)
            );
        } catch (Exception e) {
            LOGGER.errorf(e, "Failed to sync store %d to legacy system", storeId);
        }
    }
}