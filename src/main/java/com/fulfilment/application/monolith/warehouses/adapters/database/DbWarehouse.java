package com.fulfilment.application.monolith.warehouses.adapters.database;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(
        name = "warehouse",
        indexes = {
                @Index(name = "idx_warehouse_bu_code", columnList = "businessUnitCode")
        }
)
@Cacheable
public class DbWarehouse {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "warehouse_seq")
  @SequenceGenerator(name = "warehouse_seq", sequenceName = "warehouse_seq", allocationSize = 1)
  public Long id;


  @Column(name = "businessUnitCode")
  public String businessUnitCode;

  @Column(name = "location")
  public String location;

  @Column(name = "capacity")
  public Integer capacity;

  @Column(name = "stock")
  public Integer stock;

  @Column(name = "createdAt")
  public LocalDateTime createdAt;

  @Column(name = "archivedAt")
  public LocalDateTime archivedAt;

  public DbWarehouse() {
    //need to by JPA
  }
}
