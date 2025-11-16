package com.phoneclone.service;

import com.phoneclone.dto.AppInstanceRequest;
import com.phoneclone.dto.AppRequest;
import com.phoneclone.entity.App;
import com.phoneclone.entity.AppInstance;
import com.phoneclone.repository.AppInstanceRepository;
import com.phoneclone.repository.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppService {
    
    @Autowired
    private AppRepository appRepository;
    
    @Autowired
    private AppInstanceRepository appInstanceRepository;
    
    public List<App> getUserApps(Long userId) {
        return appRepository.findByUserId(userId);
    }
    
    @Transactional
    public App createApp(Long userId, AppRequest request) {
        if (appRepository.existsByUserIdAndPackageName(userId, request.getPackageName())) {
            throw new RuntimeException("应用已存在");
        }
        
        App app = new App();
        app.setUserId(userId);
        app.setPackageName(request.getPackageName());
        app.setAppName(request.getAppName());
        app.setIconUrl(request.getIconUrl());
        app.setCategory(request.getCategory());
        
        return appRepository.save(app);
    }
    
    @Transactional
    public void deleteApp(Long userId, Long appId) {
        App app = appRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("应用不存在"));
        
        if (!app.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问");
        }
        
        appRepository.delete(app);
    }
    
    public List<AppInstance> getAppInstances(Long userId, Long appId) {
        App app = appRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("应用不存在"));
        
        if (!app.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问");
        }
        
        return appInstanceRepository.findByAppId(appId);
    }
    
    @Transactional
    public AppInstance createInstance(Long userId, Long appId, AppInstanceRequest request) {
        App app = appRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("应用不存在"));
        
        if (!app.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问");
        }
        
        AppInstance instance = new AppInstance();
        instance.setAppId(appId);
        instance.setInstanceName(request.getInstanceName());
        instance.setColor(request.getColor() != null ? request.getColor() : "blue");
        instance.setStatus(AppInstance.InstanceStatus.STOPPED);
        
        return appInstanceRepository.save(instance);
    }
    
    @Transactional
    public void deleteInstance(Long userId, Long instanceId) {
        AppInstance instance = appInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("实例不存在"));
        
        App app = appRepository.findById(instance.getAppId())
                .orElseThrow(() -> new RuntimeException("应用不存在"));
        
        if (!app.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问");
        }
        
        appInstanceRepository.delete(instance);
    }
    
    @Transactional
    public AppInstance updateInstanceStatus(Long userId, Long instanceId, AppInstance.InstanceStatus status) {
        AppInstance instance = appInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("实例不存在"));
        
        App app = appRepository.findById(instance.getAppId())
                .orElseThrow(() -> new RuntimeException("应用不存在"));
        
        if (!app.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问");
        }
        
        instance.setStatus(status);
        return appInstanceRepository.save(instance);
    }
}

