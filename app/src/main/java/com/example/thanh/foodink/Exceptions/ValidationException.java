package com.example.thanh.foodink.Exceptions;

public class ValidationException extends Exception {
    private String message = "Có lỗi! Vui lòng thử lại!";

    public ValidationException() {}

    public ValidationException(String message) {
        super(message);
        this.message = message;
    }
}
