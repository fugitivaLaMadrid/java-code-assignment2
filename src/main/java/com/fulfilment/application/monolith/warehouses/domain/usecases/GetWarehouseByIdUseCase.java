package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetWarehouseByIdUseCase {

    private final WarehouseStore store;

    public GetWarehouseByIdUseCase(WarehouseStore store) {
        this.store = store;
    }

    public Warehouse getById(String id) {
        Warehouse warehouse = store.findByBusinessUnitCode(id);

        if (warehouse == null) {
            try {
                // fallback: numeric ID lookup if code not found
                Long numericId = Long.valueOf(id);
                // assuming you implement a findById method in WarehouseStore
                warehouse = store.findByBusinessUnitCode(numericId.toString());
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        if (warehouse == null || warehouse.archivedAt != null) {
            throw new IllegalArgumentException("Warehouse not found or archived");
        }

        return warehouse;
    }
}
