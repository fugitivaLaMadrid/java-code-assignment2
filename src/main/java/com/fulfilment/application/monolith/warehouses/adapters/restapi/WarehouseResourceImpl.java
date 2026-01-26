package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;

@ApplicationScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private final WarehouseRepository warehouseRepository;
  private final CreateWarehouseUseCase createWarehouseUseCase;
  private final ArchiveWarehouseUseCase archiveWarehouseUseCase;

  // Constructor injection
  public WarehouseResourceImpl(WarehouseRepository warehouseRepository,
                               CreateWarehouseUseCase createWarehouseUseCase,
                               ArchiveWarehouseUseCase archiveWarehouseUseCase) {
    this.warehouseRepository = warehouseRepository;
    this.createWarehouseUseCase = createWarehouseUseCase;

    this.archiveWarehouseUseCase = archiveWarehouseUseCase;
  }

  @Override
  public List<Warehouse> listWarehouseUnits(String location, Integer limit) {
    // Base query: only non-archived warehouses
    String query = "archivedAt is null";
    List<Object> params = new ArrayList<>();

    if (location != null && !location.isEmpty()) {
      query += " and location = ?1";
      params.add(location);
    }

    List<DbWarehouse> dbList = params.isEmpty()
            ? warehouseRepository.list(query)
            : warehouseRepository.list(query, params.toArray());

    if (limit != null && limit > 0 && limit < dbList.size()) {
      dbList = dbList.subList(0, limit);
    }

    // Map DB -> DTO using Stream.toList()
    return dbList.stream().map(db -> {
      Warehouse dto = new Warehouse();
      String bu;
      if (db.businessUnitCode != null) {
        bu = db.businessUnitCode;
      } else if (db.id != null) {
        bu = db.id.toString();
      } else {
        bu = null;
      }
      dto.setId(bu);
      dto.setLocation(db.location);
      dto.setCapacity(db.capacity);
      dto.setStock(db.stock);
      return dto;
    }).toList(); // <- Java 16+ Stream.toList()
  }


  @Override
  @Transactional
  public Warehouse createWarehouseUnit(@NotNull Warehouse data) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domain =
            new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domain.businessUnitCode = data.getId();
    domain.location = data.getLocation();
    domain.capacity = data.getCapacity();
    domain.stock = data.getStock();

    try {
      createWarehouseUseCase.create(domain);
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(e.getMessage(), 422);
    }

    Warehouse out = new Warehouse();
    out.setId(domain.businessUnitCode);
    out.setLocation(domain.location);
    out.setCapacity(domain.capacity);
    out.setStock(domain.stock);

    Response resp = Response.status(201).entity(out).type("application/json").build();
    throw new WebApplicationException(resp);
  }

  @Override
  public Warehouse getWarehouseUnitById(String id) {
    DbWarehouse db = warehouseRepository.find("businessUnitCode", id).firstResult();

    // If not found, try numeric id conversion
    if (db == null) {
      try {
        Long numeric = Long.valueOf(id);
        db = warehouseRepository.findById(numeric);
      } catch (NumberFormatException e) {
        // Log or ignore if id is not numeric
        // LOGGER.warnf("Warehouse id %s is not numeric", id);
      }
    }

    if (db == null || db.archivedAt != null) {
      throw new WebApplicationException("Warehouse with id " + id + " does not exist.", 404);
    }

    Warehouse dto = new Warehouse();
    // Clear if-else mapping
    String bu;
    if (db.businessUnitCode != null) {
      bu = db.businessUnitCode;
    } else if (db.id != null) {
      bu = db.id.toString();
    } else {
      bu = null;
    }
    dto.setId(bu);
    dto.setLocation(db.location);
    dto.setCapacity(db.capacity);
    dto.setStock(db.stock);

    return dto;
  }

  @Override
  @Transactional
  public void deleteWarehouseUnitById(String id) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domain =
            new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domain.businessUnitCode = id;

    try {
      archiveWarehouseUseCase.archive(domain);
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(e.getMessage(), 404);
    }
  }
}
