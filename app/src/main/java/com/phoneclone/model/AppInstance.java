package com.phoneclone.model;

public class AppInstance {
    private Long id;
    private Long appId;
    private String instanceName;
    private String color;
    private String status; // RUNNING, STOPPED
    
    public AppInstance() {
    }
    
    public AppInstance(Long id, Long appId, String instanceName, String color, String status) {
        this.id = id;
        this.appId = appId;
        this.instanceName = instanceName;
        this.color = color;
        this.status = status;
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
    
    public boolean isRunning() {
        return "RUNNING".equals(status);
    }
}

