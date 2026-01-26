package com.fulfilment.application.monolith.legacy;

import com.fulfilment.application.monolith.stores.Store;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class LegacyStoreManagerGateway {

  private static final Logger LOGGER = Logger.getLogger(LegacyStoreManagerGateway.class);

  public void createStoreOnLegacySystem(Store store) {
    writeToFile(store, "CREATE");
  }

  public void updateStoreOnLegacySystem(Store store) {
    writeToFile(store, "UPDATE");
  }

  private void writeToFile(Store store, String action) {
    try {
      Path tempFile = Files.createTempFile(store.getName(), ".txt");
      LOGGER.warnf("Temporary file created at: %s", tempFile);
      String content = String.format(
              "Store %s. [ name = %s ] [ items on stock = %d ]",
              action,
              store.getName(),
              store.getQuantityProductsInStock()
      );
      Files.write(tempFile, content.getBytes());
      Files.delete(tempFile);
    } catch (Exception e) {
      LOGGER.error("Failed to write Store to temporary file", e);
    }
  }
}
