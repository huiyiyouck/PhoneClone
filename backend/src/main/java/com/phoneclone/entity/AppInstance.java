package com.phoneclone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_instances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"app_id", "instance_name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "app_id", nullable = false)
    private Long appId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", insertable = false, updatable = false)
    private App app;
    
    @Column(name = "instance_name", nullable = false, length = 100)
    private String instanceName;
    
    @Column(length = 20)
    private String color = "blue";
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private InstanceStatus status = InstanceStatus.STOPPED;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum InstanceStatus {
        RUNNING, STOPPED
    }
}

