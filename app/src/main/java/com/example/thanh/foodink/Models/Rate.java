package com.example.thanh.foodink.Models;

public class Rate {
    private String content;
    private double rate;

    public Rate(String content, double rate) {
        this.content = content;
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
