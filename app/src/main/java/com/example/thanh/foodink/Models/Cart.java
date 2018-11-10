package com.example.thanh.foodink.Models;

import java.io.Serializable;

public class Cart implements Serializable {
    private int id;
    private int product_id;
    private int size_id;
    private int user_id;
    private int quantity;
    private double price;
    private Product product;
    private Size size;

    public Cart() {

    }

    public Cart(int id, int quantity, Product product, Size size) {
        this.id = id;
        this.quantity = quantity;
        this.product = product;
        this.size = size;
    }

    public Cart(int id, int product_id, int size_id, int user_id, int quantity, double price) {
        this.id = id;
        this.product_id = product_id;
        this.size_id = size_id;
        this.user_id = user_id;
        this.quantity = quantity;
        this.price = price;
    }

    public Cart(int product_id, int size_id, int user_id, int quantity, double price) {
        this.product_id = product_id;
        this.size_id = size_id;
        this.user_id = user_id;
        this.quantity = quantity;
        this.price = price;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getSize_id() {
        return size_id;
    }

    public void setSize_id(int size_id) {
        this.size_id = size_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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
}
