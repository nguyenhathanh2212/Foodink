package com.example.thanh.foodink.Models;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Size implements Serializable {
    private int id;
    private String size;
    private double price;

    public Size() {

    }

    public Size(int id, String size, double price) {
        this.id = id;
        this.size = size;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
