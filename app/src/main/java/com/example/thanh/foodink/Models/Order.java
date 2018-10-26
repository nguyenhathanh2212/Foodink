package com.example.thanh.foodink.Models;

import java.util.ArrayList;

public class Order {
    private int id;
    private String address;
    private String phone;
    private double shipCost;
    private double total;
    private String status;
    private Store store;
    private ArrayList<DetailOrder> detailOrders;

    public Order(int id, String address, String phone, double shipCost, double total, String status, Store store, ArrayList<DetailOrder> detailOrders) {
        this.id = id;
        this.address = address;
        this.phone = phone;
        this.shipCost = shipCost;
        this.total = total;
        this.status = status;
        this.store = store;
        this.detailOrders = detailOrders;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getShipCost() {
        return shipCost;
    }

    public void setShipCost(double shipCost) {
        this.shipCost = shipCost;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public ArrayList<DetailOrder> getDetailOrders() {
        return detailOrders;
    }

    public void setDetailOrders(ArrayList<DetailOrder> detailOrders) {
        this.detailOrders = detailOrders;
    }

    public String getStatusCustom() {
        switch (this.status) {
            case "pending" :{
                return "Đang chờ xử lý";
            }
            case "accepted" :{
                return "Đã xử lý";
            }
            case "rejected" :{
                return "Bị từ chối";
            }
        }

        return "";
    }
}
