package com.phoneclone.repository;

import android.content.Context;
import android.util.Log;
import com.phoneclone.database.DatabaseManager;
import com.phoneclone.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * 直接数据库访问仓库
 * 替代HTTP API调用，直接操作PostgreSQL数据库
 */
public class DirectDatabaseRepository {
    
    private static final String TAG = "DirectDatabaseRepository";
    private DatabaseManager dbManager;
    private Context context;
    
    public DirectDatabaseRepository(Context context) {
        this.context = context;
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * 初始化数据库连接
     */
    public void initializeDatabase(String dbUrl, String username, String password) {
        dbManager.initialize(dbUrl, username, password);
    }
    
    /**
     * 用户注册
     */
    public User register(String username, String email, String password) throws SQLException {
        // 检查用户是否已存在
        if (userExists(email)) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 加密密码（使用BCrypt或SHA-256）
        String passwordHash = hashPassword(password);
        
        String sql = "INSERT INTO users (username, email, password_hash, membership_level, created_at, updated_at) " +
                     "VALUES (?, ?, ?, 'FREE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING id";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Long userId = rs.getLong("id");
                    User user = new User();
                    user.setId(userId);
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setMembershipLevel("FREE");
                    return user;
                }
            }
        }
        
        throw new SQLException("注册失败");
    }
    
    /**
     * 用户登录
     */
    public User login(String email, String password) throws SQLException {
        String passwordHash = hashPassword(password);
        
        String sql = "SELECT id, username, email, membership_level FROM users " +
                     "WHERE email = ? AND password_hash = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setMembershipLevel(rs.getString("membership_level"));
                    return user;
                }
            }
        }
        
        throw new RuntimeException("邮箱或密码错误");
    }
    
    /**
     * 检查用户是否存在
     */
    private boolean userExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 简单的密码哈希（生产环境应使用BCrypt）
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e(TAG, "密码加密失败", e);
            return password; // 降级处理
        }
    }
}

