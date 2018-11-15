package com.example.thanh.foodink.Models;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Product implements Serializable{
    private int id;
    private String name;
    private String description;
    private double rate;
    private ArrayList<Size> sizes;
    private ArrayList<String> images;

//    attributes of shipper order products
    private String size;
    private String image;
    private float price;
    private String type;
    private int quantity;

    public Product() {
    }

    public Product(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Product(int id, String name, String description, double rate, ArrayList<String> images) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.images = images;
    }

    public Product(int id, String name, String description, double rate, ArrayList<Size> sizes, ArrayList<String> images) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.sizes = sizes;
        this.images = images;
    }

    public Product(String name, String size, String image, int quantity, float price, String type) {
        this.name = name;
        this.image = image;
        this.size = size;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public ArrayList<Size> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<Size> sizes) {
        this.sizes = sizes;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = (ArrayList<String>) images.clone();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        DecimalFormat numberFormat = new DecimalFormat("##,###,### đ");

        return numberFormat.format(price);
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
