package com.fulfilment.application.monolith.stores;

public class StorePatchRequest {
    private String name;
    private Integer quantityProductsInStock;

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
