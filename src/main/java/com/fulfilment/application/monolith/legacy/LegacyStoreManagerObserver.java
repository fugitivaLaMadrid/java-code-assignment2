package com.fulfilment.application.monolith.legacy;

import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.stores.StoreSavedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import org.jboss.logging.Logger;

@SuppressWarnings("unused") // SonarQube: class is used by CDI event system
@ApplicationScoped
public class LegacyStoreManagerObserver {

  private static final Logger LOGGER = Logger.getLogger(LegacyStoreManagerObserver.class);

  private final StoreRepository storeRepository;
  private final LegacyStoreManagerGateway legacyStoreManagerGateway;

  // ---------- Constructor injection ----------
  public LegacyStoreManagerObserver(StoreRepository storeRepository,
                                    LegacyStoreManagerGateway legacyStoreManagerGateway) {
    this.storeRepository = storeRepository;
    this.legacyStoreManagerGateway = legacyStoreManagerGateway;
  }

  @SuppressWarnings("squid:S2325") // Sonar: static access warning, repository style intentional
  public void onStoreSaved(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreSavedEvent event) {
    if (event == null || event.getStoreId() == null) {
      LOGGER.warn("StoreSavedEvent or storeId is null, skipping legacy sync");
      return;
    }

    // Load the Store entity using repository
    Store store = storeRepository.findByIdOptional(event.getStoreId()).orElse(null);

    if (store == null) {
      LOGGER.warnf("Store with id %d not found, skipping legacy sync", event.getStoreId());
      return;
    }

    switch (event.getAction()) {
      case CREATE:
        legacyStoreManagerGateway.createStoreOnLegacySystem(store);
        break;
      case UPDATE:
      default:
        legacyStoreManagerGateway.updateStoreOnLegacySystem(store);
        break;
    }
  }
}
