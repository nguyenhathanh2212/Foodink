package com.example.thanh.foodink.Models;

import android.content.Context;
import android.util.Log;

import com.example.thanh.foodink.Helpers.SessionManager;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private int id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String avatar;
    private String authToken;
    private int shipperId;
    private String refreshToken;
    private String expireAt;
    public static final String AUTH = "UserAuthenticate";

    public User(int id, String email, String name, String phone, String address, String avatar, String authToken, String refreshToken, String expireAt, int shipperId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.authToken = authToken;
        this.refreshToken = refreshToken;
        this.expireAt = expireAt;
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

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }

    public boolean checkTokenExpire() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            Date date = format.parse(this.expireAt);
            Date now = new Date();

            return date.before(now);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
