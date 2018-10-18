package com.example.thanh.foodink.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Store implements Serializable {
    private int id;
    private String name;
    private String description;
    private String location;
    private ArrayList<String> images;

    public Store() {
    }

    public Store(int id, String name, String description, String location, ArrayList<String> images) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.images = images;
    }

    public int getId() {
        return id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = (ArrayList<String>) images.clone();
    }
}
