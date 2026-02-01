package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.GetWarehouseByBusinessUnitCodeUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import com.warehouse.api.beans.WarehouseCreate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;

import java.util.List;

@ApplicationScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private final CreateWarehouseUseCase createWarehouseUseCase;
  private final GetWarehouseByBusinessUnitCodeUseCase getWarehouseByBusinessUnitCodeUseCase;
  private final ArchiveWarehouseUseCase archiveWarehouseUseCase;

  public WarehouseResourceImpl(
          CreateWarehouseUseCase createWarehouseUseCase,
          GetWarehouseByBusinessUnitCodeUseCase getWarehouseByBusinessUnitCodeUseCase,
          ArchiveWarehouseUseCase archiveWarehouseUseCase
  ) {
    this.createWarehouseUseCase = createWarehouseUseCase;
    this.getWarehouseByBusinessUnitCodeUseCase = getWarehouseByBusinessUnitCodeUseCase;
    this.archiveWarehouseUseCase = archiveWarehouseUseCase;
  }

  // ---------- LIST ----------
  @Override
  public List<Warehouse> listWarehouseUnits(String location, Integer limit) {
    throw new UnsupportedOperationException("List not refactored yet");
  }

  // ---------- CREATE ----------
  @Override
  @Transactional
  public Warehouse createWarehouseUnit(@NotNull WarehouseCreate data) {

    var domain = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domain.setBusinessUnitCode(data.getBusinessUnitCode());
    domain.setLocation(data.getLocation());
    domain.setCapacity(data.getCapacity());
    domain.setStock(data.getStock());

    try {
      createWarehouseUseCase.create(domain);
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(e.getMessage(), 422);
    }

    Warehouse out = new Warehouse();
    out.setBusinessUnitCode(domain.getBusinessUnitCode());
    out.setLocation(domain.getLocation());
    out.setCapacity(domain.getCapacity());
    out.setStock(domain.getStock());

    return out;
  }


  // ---------- GET ----------
  @Override
  public Warehouse getWarehouseUnitByBusinessUnitCode(String businessUnitCode) {
    try {
      var domain =
              getWarehouseByBusinessUnitCodeUseCase.getByBusinessUnitCode(businessUnitCode);

      Warehouse out = new Warehouse();
      out.setBusinessUnitCode(domain.getBusinessUnitCode());
      out.setLocation(domain.getLocation());
      out.setCapacity(domain.getCapacity());
      out.setStock(domain.getStock());

      return out;

    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(e.getMessage(), 404);
    }
  }

  // ---------- DELETE ----------
  @Override
  @Transactional
  public void deleteWarehouseUnitByBusinessUnitCode(String businessUnitCode) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domain =
            new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domain.setBusinessUnitCode(businessUnitCode);

    try {
      archiveWarehouseUseCase.archiveByBusinessUnitCode(domain.toString());
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(e.getMessage(), 404);
    }

  }

}
