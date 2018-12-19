package com.example.thanh.foodink.Configs;

public interface ApiUrl {
    public static final String API_URL = "https://fudink.herokuapp.com/api/";

    public static final String API_LOGIN = API_URL + "login";
    public static final String API_CHANGEPASS = API_URL + "users/password";
    public static final String API_LOGOUT = API_URL + "logout";
    public static final String API_REGISTER = API_URL + "signup";
    public static final String API_LOGIN_REFRESH = API_URL + "refresh";
    public static final String API_STORES = "https://fudink.herokuapp.com/api/stores";
    public static final String API_CATEGORIES = "https://fudink.herokuapp.com/api/categories";
    public static final String API_SHIPPER_ORDER_LIST = API_URL + "dashboard/shipper_orders";
    public static final String API_CARTS = "https://fudink.herokuapp.com/api/carts";
    public static final String API_ORDERS = "https://fudink.herokuapp.com/api/orders";
    public static final String API_CHANGE_SHIPPER_STATUS = API_URL + "dashboard/devices";
    public static final String API_SHIPPER = API_URL + "dashboard/shippers";
    public static final String API_ORDER = API_URL + "orders";
    public static final String API_PRODUCT = API_URL + "products";
}
