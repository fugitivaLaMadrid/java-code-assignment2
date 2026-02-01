package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.JpaStore;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ListWarehousesUseCase {

    private final WarehouseStore store;

    public ListWarehousesUseCase(@JpaStore WarehouseStore store) {
        this.store = store;
    }

    public List<Warehouse> list(String location, Integer limit) {
        List<Warehouse> warehouses;

        if (location != null && !location.isEmpty()) {
            // Get warehouses at the given location
            warehouses = store.findByLocation(location);
        } else {
            // No location filter: get all active warehouses
            warehouses = store.findByLocation(null); // or implement store.listActive()
        }

        // Apply limit if provided
        if (limit != null && limit > 0 && limit < warehouses.size()) {
            return warehouses.subList(0, limit);
        }

        return warehouses;
    }
}
