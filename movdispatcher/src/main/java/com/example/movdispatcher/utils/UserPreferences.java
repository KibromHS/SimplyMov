package com.example.movdispatcher.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movdispatcher.models.Dispatcher;

public class UserPreferences {
    private static SharedPreferences preferences;
    private static final String PREF_KEY = "key-data-dispatcher";
    private static final String USER_KEY = "key-user-dispatcher";

    public static void init(@NonNull Context context) {
        preferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    public static void setUser(@NonNull Dispatcher user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_KEY, user.toJson());
        editor.apply();
    }

    @Nullable
    public static Dispatcher getUser() {
        String json = preferences.getString(USER_KEY, null);
        return json == null ? null : Dispatcher.fromJson(json);
    }

    public static void clearPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
