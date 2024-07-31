package com.example.driver.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.driver.models.Driver;

public class UserPreferences {
    private static SharedPreferences preferences;
    private static final String PREF_KEY = "key-data-driver";
    private static final String USER_KEY = "key-user-driver";

    public static void init(@NonNull Context context) {
        preferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    public static void setUser(@NonNull Driver user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_KEY, user.toJson());
        editor.apply();
    }

    @Nullable
    public static Driver getUser() {
        String json = preferences.getString(USER_KEY, null);
        return json == null ? null : Driver.fromJson(json);
    }

    public static void clearPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
