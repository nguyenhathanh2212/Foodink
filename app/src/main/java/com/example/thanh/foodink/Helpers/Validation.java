package com.example.thanh.foodink.Helpers;

import android.widget.EditText;

import java.util.regex.Pattern;

public class Validation {

    public static boolean isValidEmaillId(String email) {
        return Pattern.compile("^[\\w+\\-.]+@[a-z\\d\\-.]+\\.[a-z]+$").matcher(email).matches();
    }

    public static boolean checkEmptyInput(EditText editText) {
        return editText.getText().toString().trim().equals("");
    }

    public static boolean isPhoneNumber(String number) {
        return Pattern.compile("^\\A0\\d{9,10}$").matcher(number).matches();
    }
}
