package com.fulfilment.application.monolith.warehouses.domain.models;

public class Location {
  private String identification;

  // maximum number of warehouses that can be created in this location
  private int maxNumberOfWarehouses;

  // maximum capacity of the location summing all the warehouse capacities
  private int maxCapacity;


  public Location(String identification, int maxNumberOfWarehouses, int maxCapacity) {
    this.identification = identification;
    this.maxNumberOfWarehouses = maxNumberOfWarehouses;
    this.maxCapacity = maxCapacity;
  }

  public String getIdentification() {
    return identification;
  }

  public void setIdentification(String identifier) {
    this.identification = identifier;
  }

  public int getMaxNumberOfWarehouses() {
    return maxNumberOfWarehouses;
  }

  public void setMaxNumberOfWarehouses(int maxNumberOfWarehouses) {
    this.maxNumberOfWarehouses = maxNumberOfWarehouses;
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  public void setMaxCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
  }

}
