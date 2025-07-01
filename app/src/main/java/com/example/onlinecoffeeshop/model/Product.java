package com.example.onlinecoffeeshop.model;

import java.util.List;

public class Product {
    private String title;
    private String description;
    private String extra;
    private List<String> picUrl;
    private double price;
    private double rating;
    private String categoryId;

    public Product(String title, String description, String extra, List<String> picUrl, double price, double rating, String categoryId) {
        this.title = title;
        this.description = description;
        this.extra = extra;
        this.picUrl = picUrl;
        this.price = price;
        this.rating = rating;
        this.categoryId = categoryId;
    }

    public Product() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public List<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(List<String> picUrl) {
        this.picUrl = picUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
