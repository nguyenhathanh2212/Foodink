package com.example.thanh.foodink.Models;

import java.text.DecimalFormat;

/**
 * Created by Vinh Nguyen on 11/1/2018.
 */

public class ShipperOrder {
    private int orderID;
    private String storeName;
    private String receiverName;
    private float shipCost;
    private String orderStatus;
    private String storeImage;

    public ShipperOrder(int orderID, String storeName, String receiverName, float shipCost, String orderStatus, String storeImage) {
        this.orderID = orderID;
        this.storeName = storeName;
        this.receiverName = receiverName;
        this.shipCost = shipCost;
        this.orderStatus = orderStatus;
        this.storeImage = storeImage;
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

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getShipCost() {
        DecimalFormat numberFormat = new DecimalFormat("##,###,### đ");

        return numberFormat.format(shipCost);
    }

    public void setShipCost(float shipCost) {
        this.shipCost = shipCost;
    }

    public String getOrderStatus() {
        if (orderStatus.equals("shipping")) {
            return "Đang vận chuyển";
        }

        if (orderStatus.equals("done")) {
            return "Hoàn thành";
        }

        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }
}
