package com.example.onlinecoffeeshop.model;

public class Product {
    private String product_id;
    private String name;
    private String category_id;
    private double price;
    private String image_url;
    private boolean is_active;
    private String created_at;

    public Product() { }
    public Product(String product_id, String name, String category_id, double price, String image_url, boolean is_active, String created_at) {
        this.product_id = product_id;
        this.name = name;
        this.category_id = category_id;
        this.price = price;
        this.image_url = image_url;
        this.is_active = is_active;
        this.created_at = created_at;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
