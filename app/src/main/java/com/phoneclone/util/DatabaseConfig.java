package com.phoneclone.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 数据库配置管理器
 * 存储和管理数据库连接信息
 */
public class DatabaseConfig {
    
    private static final String PREFS_NAME = "db_config";
    private static final String KEY_DB_URL = "db_url";
    private static final String KEY_DB_USERNAME = "db_username";
    private static final String KEY_DB_PASSWORD = "db_password";
    
    private SharedPreferences prefs;
    
    public DatabaseConfig(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 保存数据库配置
     */
    public void saveConfig(String url, String username, String password) {
        prefs.edit()
                .putString(KEY_DB_URL, url)
                .putString(KEY_DB_USERNAME, username)
                .putString(KEY_DB_PASSWORD, password)
                .apply();
    }
    
    /**
     * 获取数据库URL
     */
    public String getDbUrl() {
        return prefs.getString(KEY_DB_URL, null);
    }
    
    /**
     * 获取数据库用户名
     */
    public String getDbUsername() {
        return prefs.getString(KEY_DB_USERNAME, null);
    }
    
    /**
     * 获取数据库密码
     */
    public String getDbPassword() {
        return prefs.getString(KEY_DB_PASSWORD, null);
    }
    
    /**
     * 检查是否已配置
     */
    public boolean isConfigured() {
        return getDbUrl() != null && getDbUsername() != null && getDbPassword() != null;
    }
    
    /**
     * 清除配置
     */
    public void clear() {
        prefs.edit().clear().apply();
    }
}

