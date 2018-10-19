package com.example.thanh.foodink.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCES_NAME = "session";
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void set(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void forget(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void destroy() {
        editor.clear();
        editor.apply();
    }
}
