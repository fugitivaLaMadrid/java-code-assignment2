package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {


  private static final String BUSINESS_UNICODE = "businessUnitCode";

  private Warehouse toDomain(DbWarehouse db) {
    if (db == null) return null;
    Warehouse w = new Warehouse();
    w.businessUnitCode = db.id == null ? null : String.valueOf(db.id);
    w.location = db.location;
    w.capacity = db.capacity;
    w.stock = db.stock;
    if (db.createdAt != null) w.creationAt = db.createdAt.atZone(ZoneId.systemDefault());
    if (db.archivedAt != null) w.archivedAt = db.archivedAt.atZone(ZoneId.systemDefault());
    return w;
  }

  private DbWarehouse toDb(Warehouse w) {
    DbWarehouse db = new DbWarehouse();
    // businessUnitCode is represented as generated id in DbWarehouse; the domain businessUnitCode should be mapped externally
    db.location = w.location;
    db.capacity = w.capacity;
    db.stock = w.stock;
    if (w.creationAt != null) db.createdAt = LocalDateTime.now();
    return db;
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse db = new DbWarehouse();
    db.businessUnitCode = warehouse.businessUnitCode;
    db.location = warehouse.location;
    db.capacity = warehouse.capacity;
    db.stock = warehouse.stock;
    db.createdAt = LocalDateTime.now();
    persist(db);
    // set generated id back to domain BU code if desired
    warehouse.businessUnitCode = db.id != null ? db.id.toString() : warehouse.businessUnitCode;
  }

  @Override
  public void update(Warehouse warehouse) {
    // find db entry by businessUnitCode
    DbWarehouse db = find(BUSINESS_UNICODE, warehouse.businessUnitCode).firstResult();
    if (db == null) return;
    db.location = warehouse.location;
    db.capacity = warehouse.capacity;
    db.stock = warehouse.stock;
    if (warehouse.archivedAt != null) db.archivedAt = warehouse.archivedAt.toLocalDateTime();
    persist(db);
  }

  @Override
  public void remove(Warehouse warehouse) {
    // archive flag instead of delete
    DbWarehouse db = find(BUSINESS_UNICODE, warehouse.businessUnitCode).firstResult();
    if (db == null) return;
    db.archivedAt = LocalDateTime.now();
    persist(db);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse db = find(BUSINESS_UNICODE, buCode).firstResult();
    return toDomain(db);
  }

  @Override
  public long countByLocation(String location) {
    return count("location = ?1 and archivedAt is null", location);
  }

  @Override
  public int sumCapacityByLocation(String location) {
    Number res = (Number) getEntityManager()
            .createQuery("select coalesce(sum(d.capacity),0) from DbWarehouse d where d.location = :loc and d.archivedAt is null")
            .setParameter("loc", location)
            .getSingleResult();
    return res.intValue();
  }

  @Override
  public List<Warehouse> findByLocation(String location) {
    return list("location = ?1 and archivedAt is null", location)
            .stream()
            .map(this::toDomain)
            .toList();
  }

  //Add a helper to list all non-archived warehouses for the GET endpoint
  public List<Warehouse> findByLocationAll() {
    return list("archivedAt is null").stream().map(this::toDomain).toList();
  }

}
