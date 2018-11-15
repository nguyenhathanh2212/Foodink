package com.example.thanh.foodink.Models;

import java.text.DecimalFormat;

public class Notification {
    private int orderID;
    private String storeName;
    private String storeAddress;
    private String imgStore;
    private float distanceToStore;
    private float shipCost;

    public Notification(int orderID, String storeName, String storeAddress, String imgStore, float distanceToStore, float shipCost) {
        this.orderID = orderID;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.imgStore = imgStore;
        this.distanceToStore = distanceToStore;
        this.shipCost = shipCost;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getImgStore() {
        return imgStore;
    }

    public void setImgStore(String imgStore) {
        this.imgStore = imgStore;
    }

    public float getDistanceToStore() {
        return distanceToStore;
    }

    public void setDistanceToStore(float distanceToStore) {
        this.distanceToStore = distanceToStore;
    }

    public String getShipCost() {
        DecimalFormat numberFormat = new DecimalFormat("##,###,### đ");

        return numberFormat.format(shipCost);
    }

    public void setShipCost(float shipCost) {
        this.shipCost = shipCost;
    }
}
