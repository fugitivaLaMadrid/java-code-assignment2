package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@JpaStore
public class JpaWarehouseStore implements WarehouseStore {

    private static final String BUSINESS_CODE = "buCode";
    private final EntityManager em;
    private final WarehouseRepository repository;

    @Inject
    public JpaWarehouseStore(EntityManager em, WarehouseRepository repository) {
        this.em = em;
        this.repository = repository;
    }

    @Override
    @Transactional
    public void create(Warehouse warehouse) {
        DbWarehouse db = toDbWarehouse(warehouse);
        em.persist(db);
        em.flush();
        warehouse.setBusinessUnitCode(db.businessUnitCode); // copy back ID/code
    }

    @Override
    @Transactional
    public void update(Warehouse warehouse) {
        DbWarehouse db = em.createQuery(
                        "SELECT w FROM DbWarehouse w WHERE w.businessUnitCode = :buCode", DbWarehouse.class)
                .setParameter(BUSINESS_CODE, warehouse.getBusinessUnitCode())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (db != null) {
            db.location = warehouse.getLocation();
            db.capacity = warehouse.getCapacity();
            db.stock = warehouse.getStock();
            em.merge(db);
        }
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
        DbWarehouse db = em.createQuery(
                        "SELECT w FROM DbWarehouse w WHERE w.businessUnitCode = :buCode AND w.archivedAt IS NULL", DbWarehouse.class)
                .setParameter(BUSINESS_CODE, buCode)
                .getResultStream()
                .findFirst()
                .orElse(null);

        return db == null ? null : toDomain(db);
    }

    @Override
    public long countByLocation(String location) {
        return em.createQuery(
                        "SELECT COUNT(w) FROM DbWarehouse w WHERE w.location = :loc AND w.archivedAt IS NULL", Long.class)
                .setParameter("loc", location)
                .getSingleResult();
    }

    @Override
    public int sumCapacityByLocation(String location) {
        Long sum = em.createQuery(
                        "SELECT SUM(w.capacity) FROM DbWarehouse w WHERE w.location = :loc AND w.archivedAt IS NULL", Long.class)
                .setParameter("loc", location)
                .getSingleResult();
        return sum != null ? sum.intValue() : 0;
    }

    @Override
    public List<Warehouse> findByLocation(String location) {
        List<DbWarehouse> dbList = em.createQuery(
                        "SELECT w FROM DbWarehouse w WHERE w.location = :loc AND w.archivedAt IS NULL", DbWarehouse.class)
                .setParameter("loc", location)
                .getResultList();

        List<Warehouse> result = new ArrayList<>();
        for (DbWarehouse db : dbList) {
            result.add(toDomain(db));
        }
        return result;
    }

    @Override
    @Transactional
    public void save(Warehouse warehouse) {
        Warehouse existing = findByBusinessUnitCode(warehouse.getBusinessUnitCode());
        if (existing == null) {
            create(warehouse);
        } else {
            update(warehouse);
        }
    }


    @Override
    @Transactional
    public void archiveByBusinessUnitCode(String buCode) {
        int updated = em.createQuery("""
        UPDATE DbWarehouse w
        SET w.archivedAt = :now
        WHERE w.businessUnitCode = :buCode
        AND w.archivedAt IS NULL
    """)
                .setParameter("now", LocalDateTime.now())
                .setParameter(BUSINESS_CODE, buCode)
                .executeUpdate();

        em.flush(); // ensure changes are visible in the persistence context

        if (updated == 0) {
            throw new IllegalArgumentException("Warehouse not found or already archived");
        }
    }


    // ---------------------- Helpers ----------------------
    private DbWarehouse toDbWarehouse(Warehouse w) {
        DbWarehouse db = new DbWarehouse();
        db.businessUnitCode = w.getBusinessUnitCode();
        db.location = w.getLocation();
        db.capacity = w.getCapacity();
        db.stock = w.getStock();
        db.createdAt = LocalDateTime.now();
        return db;
    }

    private Warehouse toDomain(DbWarehouse db) {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode(db.businessUnitCode);
        w.setLocation(db.location);
        w.setCapacity(db.capacity);
        w.setStock(db.stock);
        w.setArchivedAt(db.archivedAt != null ? db.archivedAt.atZone(ZoneId.systemDefault()) : null);
        w.setCreationAt(db.createdAt != null ? db.createdAt.atZone(ZoneId.systemDefault()) : ZonedDateTime.now());
        return w;
    }

}
