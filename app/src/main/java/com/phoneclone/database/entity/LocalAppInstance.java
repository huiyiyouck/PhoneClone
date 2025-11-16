package com.phoneclone.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_app_instances")
public class LocalAppInstance {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    
    private Long appId; // 本地App ID
    private String instanceName;
    private String color;
    private String status; // RUNNING, STOPPED
    private Long serverId; // 服务器端ID
    private boolean synced; // 是否已同步
    
    public LocalAppInstance() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAppId() {
        return appId;
    }
    
    public void setAppId(Long appId) {
        this.appId = appId;
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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

