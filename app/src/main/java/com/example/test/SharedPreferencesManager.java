package com.example.test;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_OTP_ID = "otp_id";

    private static SharedPreferencesManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    public void saveID(String id) {
        sharedPreferences.edit().putString(KEY_USER_ID, id).apply();
    }

    public String getID() {
        return sharedPreferences.getString(KEY_USER_ID, "unknown_user");
    }

    public void saveOTP_ID(String otpID) {
        sharedPreferences.edit().putString(KEY_OTP_ID, otpID).apply();
    }

    public String getOTP_ID() {
        return sharedPreferences.getString(KEY_OTP_ID, "unknown_otp");
    }
}

