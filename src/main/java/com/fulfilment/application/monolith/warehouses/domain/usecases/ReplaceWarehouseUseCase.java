package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.JpaStore;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public ReplaceWarehouseUseCase(
          @JpaStore WarehouseStore warehouseStore,
          LocationResolver locationResolver
  ) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {

    // 1️ Existing warehouse must exist
    Warehouse existing =
            warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode());

    if (existing == null) {
      throw new IllegalArgumentException(
              "Warehouse not found: " + newWarehouse.getBusinessUnitCode()
      );
    }

    // 2. Business Unit Code must be reused (replace, not create-new)
    if (!existing.getBusinessUnitCode()
            .equals(newWarehouse.getBusinessUnitCode())) {
      throw new IllegalArgumentException("BusinessUnitCode cannot be changed");
    }

    // 3. Stock must match (replace must not mutate stock)
    if (existing.getStock() != null
            && !existing.getStock().equals(newWarehouse.getStock())) {
      throw new IllegalArgumentException(
              "Stock mismatch: existing=" + existing.getStock()
                      + ", new=" + newWarehouse.getStock()
      );
    }

    // 4.Location must exist
    Location location =
            locationResolver.resolveByIdentifier(newWarehouse.getLocation());

    if (location == null) {
      throw new IllegalArgumentException(
              "Location not found: " + newWarehouse.getLocation()
      );
    }

    // 5. Capacity must fit within location limits
    if (newWarehouse.getCapacity() > location.getMaxCapacity()) {
      throw new IllegalArgumentException(
              "Capacity exceeds location limit: "
                      + newWarehouse.getCapacity()
                      + " > " + location.getMaxCapacity()
      );
    }

    // 6. Archive old warehouse
    warehouseStore.archiveByBusinessUnitCode(existing.getBusinessUnitCode());

    // 7. Create replacement warehouse (same BU code, new row)
    warehouseStore.create(newWarehouse);
  }
}