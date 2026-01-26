package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.util.List;

public interface WarehouseStore {
  void create(Warehouse warehouse);

  void update(Warehouse warehouse);

  void remove(Warehouse warehouse);

  Warehouse findByBusinessUnitCode(String buCode);

  // number of non-archived warehouses at a location
  long countByLocation(String location);

  // total capacity (sum of capacities) of non-archived warehouses at a location
  int sumCapacityByLocation(String location);

  // list warehouses at a location (non-archived)
  List<Warehouse> findByLocation(String location);
}
