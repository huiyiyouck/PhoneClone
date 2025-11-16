package com.phoneclone.database;

import android.content.Context;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接管理器
 * 直接连接PostgreSQL数据库
 */
public class DatabaseManager {
    
    private static final String TAG = "DatabaseManager";
    private static DatabaseManager instance;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    
    private DatabaseManager() {
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * 初始化数据库连接配置
     */
    public void initialize(String url, String username, String password) {
        this.dbUrl = url;
        this.dbUsername = username;
        this.dbPassword = password;
    }
    
    /**
     * 获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            throw new SQLException("数据库未初始化，请先调用initialize()");
        }
        
        try {
            // 加载PostgreSQL驱动
            Class.forName("org.postgresql.Driver");
            
            Properties props = new Properties();
            props.setProperty("user", dbUsername);
            props.setProperty("password", dbPassword);
            props.setProperty("ssl", "true");
            
            Connection conn = DriverManager.getConnection(dbUrl, props);
            Log.d(TAG, "数据库连接成功");
            return conn;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "PostgreSQL驱动未找到", e);
            throw new SQLException("PostgreSQL驱动未找到", e);
        } catch (SQLException e) {
            Log.e(TAG, "数据库连接失败", e);
            throw e;
        }
    }
    
    /**
     * 测试数据库连接
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            Log.e(TAG, "数据库连接测试失败", e);
            return false;
        }
    }
}

