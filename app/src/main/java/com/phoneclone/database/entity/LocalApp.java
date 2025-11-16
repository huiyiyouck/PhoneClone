package com.phoneclone.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_apps")
public class LocalApp {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    
    private String packageName;
    private String appName;
    private String iconUrl;
    private String category;
    private Long serverId; // 服务器端ID，用于同步
    private boolean synced; // 是否已同步
    
    public LocalApp() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getAppName() {
        return appName;
    }
    
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Long getServerId() {
        return serverId;
    }
    
    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }
    
    public boolean isSynced() {
        return synced;
    }
    
    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}

