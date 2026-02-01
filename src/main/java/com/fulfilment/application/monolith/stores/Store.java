package com.fulfilment.application.monolith.stores;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Cacheable
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_seq")
  @SequenceGenerator(name = "store_seq", sequenceName = "store_seq", allocationSize = 1)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @Column(length = 40, unique = true, nullable = false)
  private String name;

  @Column
  private Integer quantityProductsInStock; // changed from int → Integer

  // ---------- Default constructor required by JPA ----------
  public Store() {}

  // ---------- Convenience constructor ----------
  public Store(String name) {
    this.name = name;
  }

  // ---------- Getters & Setters ----------
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getQuantityProductsInStock() {
    return quantityProductsInStock;
  }

  public void setQuantityProductsInStock(Integer quantityProductsInStock) {
    this.quantityProductsInStock = quantityProductsInStock;
  }
}
