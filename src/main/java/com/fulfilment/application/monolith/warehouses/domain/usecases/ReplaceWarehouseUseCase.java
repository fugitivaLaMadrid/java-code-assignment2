package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  /**
   * Constructor injection (recommended)
   */
  @Inject
  public ReplaceWarehouseUseCase(
          WarehouseStore warehouseStore,
          LocationResolver locationResolver
  ) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse warehouse) {

    if (warehouse.businessUnitCode == null) {
      throw new IllegalArgumentException("Business Unit Code must be provided");
    }

    Warehouse existing =
            warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);

    if (existing == null) {
      throw new IllegalArgumentException(
              "Warehouse not found: " + warehouse.businessUnitCode);
    }

    if (warehouse.location == null) {
      throw new IllegalArgumentException("Location must be provided");
    }

    Location location;
    try {
      location = locationResolver.resolveByIdentifier(warehouse.location);
    } catch (Exception e) {
      throw new IllegalArgumentException(
              "Invalid location: " + warehouse.location, e);
    }

    if (location == null) {
      throw new IllegalArgumentException(
              "Invalid location: " + warehouse.location);
    }

    if (warehouse.capacity == null || warehouse.capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be positive");
    }

    if (warehouse.stock == null || warehouse.stock < 0) {
      throw new IllegalArgumentException("Stock must be non-negative");
    }

    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException(
              "Stock cannot exceed warehouse capacity");
    }

    warehouseStore.update(warehouse);
  }
}
