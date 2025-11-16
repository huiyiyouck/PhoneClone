package com.phoneclone.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MEMBERSHIP_LEVEL = "membership_level";
    
    private SharedPreferences sharedPreferences;
    
    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    
    public void saveUserInfo(String username, String email, String membershipLevel) {
        sharedPreferences.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL, email)
                .putString(KEY_MEMBERSHIP_LEVEL, membershipLevel)
                .apply();
    }
    
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }
    
    public String getMembershipLevel() {
        return sharedPreferences.getString(KEY_MEMBERSHIP_LEVEL, null);
    }
    
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
    
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}

