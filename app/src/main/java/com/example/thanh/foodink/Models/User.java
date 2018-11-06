package com.example.thanh.foodink.Models;

import android.content.Context;

import com.example.thanh.foodink.Helpers.SessionManager;
import com.google.gson.Gson;

public class User {
    private int id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String avatar;
    private String authToken;
    private int shipperId;
    public static final String AUTH = "UserAuthenticate";

    public User(int id, String email, String name, String phone, String address, String avatar, String authToken, int shipperId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.authToken = authToken;
        this.shipperId = shipperId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static void setUserAuth(Context context, User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.set(User.AUTH, userJson);
    }

    public static User getUserAuth(Context context) {
        Gson gson = new Gson();
        SessionManager sessionManager = new SessionManager(context);
        String userJson = sessionManager.get(User.AUTH);

        return gson.fromJson(userJson, User.class);
    }

    public static boolean checkUserAuth(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        String userJson = sessionManager.get(User.AUTH);

        return !userJson.equals("");
    }

    public static void logout(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.forget(User.AUTH);
    }

    public int getShipperId() {
        return shipperId;
    }

    public void setShipperId(int shipperId) {
        this.shipperId = shipperId;
    }
}
