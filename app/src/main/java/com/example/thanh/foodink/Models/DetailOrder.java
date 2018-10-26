package com.example.thanh.foodink.Models;

import java.util.ArrayList;

public class DetailOrder {
    private int id;
    private int quantity;
    private double price;
    private double total;
    private Product product;
    private Size size;
    private ArrayList<String> images;

    public DetailOrder(int id, int quantity, double price, double total, Product product, Size size, ArrayList<String> images) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.product = product;
        this.size = size;
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
