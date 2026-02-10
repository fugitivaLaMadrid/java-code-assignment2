package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.JpaStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ArchiveWarehouseUseCase {

  private final WarehouseStore warehouseStore;

  @Inject
  public ArchiveWarehouseUseCase(@JpaStore WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Transactional
  public void archiveByBusinessUnitCode(String businessUnitCode) {
    var warehouse = warehouseStore.findByBusinessUnitCode(businessUnitCode);

    if (warehouse == null) {
      throw new IllegalArgumentException(
              "Warehouse not found: " + businessUnitCode
      );
    }

    warehouseStore.archiveByBusinessUnitCode(businessUnitCode);
  }
}
