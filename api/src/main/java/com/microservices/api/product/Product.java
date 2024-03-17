package com.microservices.api.product;

public class Product {
    private final int productId;
    private final String name;
    private final int weight;
    private final String serviceASddress;

    public Product(){
        productId = 0;
        name = null;
        weight = 0;
        serviceASddress = null;
    }

    public Product(int productId, String name, int weight, String serviceASddress) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.serviceASddress = serviceASddress;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public String getServiceASddress() {
        return serviceASddress;
    }
}
