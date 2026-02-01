package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.JpaStore;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetWarehouseByBusinessUnitCodeUseCase {

    private final WarehouseStore store;

    public GetWarehouseByBusinessUnitCodeUseCase(@JpaStore WarehouseStore store) {
        this.store = store;
    }

    public Warehouse getByBusinessUnitCode(String businessUnitCode) {

        Warehouse warehouse = store.findByBusinessUnitCode(businessUnitCode);

        if (warehouse == null || warehouse.getArchivedAt() != null) {
            throw new IllegalArgumentException("Warehouse not found or archived");
        }

        return warehouse;
    }
}
