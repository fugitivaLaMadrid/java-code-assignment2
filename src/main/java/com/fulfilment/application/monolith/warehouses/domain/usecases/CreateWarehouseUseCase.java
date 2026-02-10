package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.JpaStore;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public CreateWarehouseUseCase(@JpaStore WarehouseStore warehouseStore,
                                LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Transactional
  @Override
  public void create(Warehouse warehouse) {
    // Business Unit Code uniqueness
    if (warehouse.getBusinessUnitCode() != null &&
            warehouseStore.findByBusinessUnitCode(warehouse.getBusinessUnitCode()) != null) {
      throw new IllegalArgumentException("Business Unit Code already exists: " + warehouse.getBusinessUnitCode());
    }

    // Validate location
    if (warehouse.getLocation() == null) {
      throw new IllegalArgumentException("Location must be provided");
    }
    Location location;
    try {
      location = locationResolver.resolveByIdentifier(warehouse.getLocation());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid location: " + warehouse.getLocation(), e);
    }
    if (location == null) {
      throw new IllegalArgumentException("Invalid location: " + warehouse.getLocation());
    }

    // Check number of warehouses in location
    long existing = warehouseStore.countByLocation(warehouse.getLocation());
    if (existing >= location.getMaxNumberOfWarehouses()) {
      throw new IllegalArgumentException("Maximum number of warehouses reached for location: " + warehouse.getLocation());
    }

    // Capacity & stock validation
    if (warehouse.getCapacity() == null || warehouse.getCapacity() <= 0) {
      throw new IllegalArgumentException("Capacity must be positive");
    }
    if (warehouse.getStock() == null || warehouse.getStock() < 0) {
      throw new IllegalArgumentException("Stock must be non-negative");
    }
    int usedCapacity = warehouseStore.sumCapacityByLocation(warehouse.getLocation());
    if (usedCapacity + warehouse.getCapacity() > location.getMaxCapacity()) {
      throw new IllegalArgumentException("Capacity exceeds location maximum capacity");
    }
    if (warehouse.getStock() > warehouse.getCapacity()) {
      throw new IllegalArgumentException("Stock cannot exceed warehouse capacity");
    }

    // Create the warehouse
    warehouseStore.create(warehouse);
  }
}
