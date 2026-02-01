package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.JpaStore;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArchiveWarehouseUseCase {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(@JpaStore WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  public void archiveByBusinessUnitCode(String businessUnitCode) {

    Warehouse w = warehouseStore.findByBusinessUnitCode(businessUnitCode);

    if (w == null) {
      throw new IllegalArgumentException("Warehouse not found");
    }

    // idempotent delete
    warehouseStore.archiveByBusinessUnitCode(w.getBusinessUnitCode());
  }
}
