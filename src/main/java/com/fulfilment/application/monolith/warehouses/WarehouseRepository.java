package com.fulfilment.application.monolith.warehouses;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;

import java.util.List;

public interface WarehouseRepository {
    // Find non-archived warehouses
    List<DbWarehouse> listActive();

    // Find by business unit code
    DbWarehouse findByBusinessUnitCode(String code);

    // Find by numeric ID
    DbWarehouse findById(Long id);

    // Save or update
    void save(DbWarehouse warehouse);

    // Archive warehouse
    void archiveByBusinessUnitCode(DbWarehouse warehouse);

    List<DbWarehouse> findByLocation(String location);

}
