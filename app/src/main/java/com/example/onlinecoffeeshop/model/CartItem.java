package com.example.onlinecoffeeshop.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productId;
    private String title;
    private String imageUrl;
    private double price;
    private int quantity;

    public CartItem() {} // bắt buộc

    // Getter và Setter
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}


