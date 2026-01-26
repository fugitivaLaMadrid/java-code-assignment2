package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  // Constructor for both CDI injection and unit tests
  @Inject
  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    // Business Unit Code uniqueness
    if (warehouse.businessUnitCode != null && warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new IllegalArgumentException("Business Unit Code already exists: " + warehouse.businessUnitCode);
    }

    // Location validation
    if (warehouse.location == null) {
      throw new IllegalArgumentException("Location must be provided");
    }

    Location location;
    try {
      location = locationResolver.resolveByIdentifier(warehouse.location);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid location: " + warehouse.location, e);
    }

    if (location == null) {
      throw new IllegalArgumentException("Invalid location: " + warehouse.location);
    }

    // Number of warehouses feasibility
    long existing = warehouseStore.countByLocation(warehouse.location);
    if (existing >= location.getMaxNumberOfWarehouses()) {
      throw new IllegalArgumentException("Maximum number of warehouses reached for location: " + warehouse.location);
    }

    // Capacity and stock validation
    if (warehouse.capacity == null || warehouse.capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be positive");
    }
    if (warehouse.stock == null || warehouse.stock < 0) {
      throw new IllegalArgumentException("Stock must be non-negative");
    }
    int used = warehouseStore.sumCapacityByLocation(warehouse.location);
    if (used + warehouse.capacity > location.getMaxCapacity()) {
      throw new IllegalArgumentException("Capacity exceeds location maximum capacity");
    }
    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException("Stock cannot exceed warehouse capacity");
    }

    // Create warehouse
    warehouseStore.create(warehouse);
  }
}
