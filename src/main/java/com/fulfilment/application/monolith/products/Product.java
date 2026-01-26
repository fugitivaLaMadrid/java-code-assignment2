package com.fulfilment.application.monolith.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Cacheable
public class Product {

  @Id @GeneratedValue
  @JsonProperty
  public Long id;

  @Column(length = 40, unique = true)
  @JsonProperty
  public String name;

  @Column(nullable = true)
  @JsonProperty
  public String description;

  @Column(precision = 10, scale = 2, nullable = true)
  @JsonProperty
  public BigDecimal price;

  @JsonProperty
  public int stock;

  public Product() {}

  public Product(String name) {
    this.name = name;
  }
}
