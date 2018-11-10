package com.example.thanh.foodink.Helpers;

import android.widget.EditText;

import java.util.regex.Pattern;

public class Validation {

    public static boolean isValidEmaillId(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean checkEmptyInput(EditText editText) {
        return editText.getText().toString().trim().equals("");
    }

    public static boolean isPhoneNumber(String number) {
        return Pattern.compile("^\\A0\\d{9,10}$").matcher(number).matches();
    }
}
