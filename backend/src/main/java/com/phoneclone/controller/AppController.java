package com.phoneclone.controller;

import com.phoneclone.dto.AppInstanceRequest;
import com.phoneclone.dto.AppRequest;
import com.phoneclone.entity.App;
import com.phoneclone.entity.AppInstance;
import com.phoneclone.service.AppService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apps")
public class AppController {
    
    @Autowired
    private AppService appService;
    
    @Autowired
    private com.phoneclone.repository.UserRepository userRepository;
    
    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(com.phoneclone.entity.User::getId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    @GetMapping
    public ResponseEntity<List<App>> getUserApps(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<App> apps = appService.getUserApps(userId);
        return ResponseEntity.ok(apps);
    }
    
    @PostMapping
    public ResponseEntity<App> createApp(
            @Valid @RequestBody AppRequest request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        App app = appService.createApp(userId, request);
        return ResponseEntity.ok(app);
    }
    
    @DeleteMapping("/{appId}")
    public ResponseEntity<Void> deleteApp(
            @PathVariable Long appId,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        appService.deleteApp(userId, appId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{appId}/instances")
    public ResponseEntity<List<AppInstance>> getAppInstances(
            @PathVariable Long appId,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<AppInstance> instances = appService.getAppInstances(userId, appId);
        return ResponseEntity.ok(instances);
    }
    
    @PostMapping("/{appId}/instances")
    public ResponseEntity<AppInstance> createInstance(
            @PathVariable Long appId,
            @Valid @RequestBody AppInstanceRequest request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        AppInstance instance = appService.createInstance(userId, appId, request);
        return ResponseEntity.ok(instance);
    }
    
    @DeleteMapping("/instances/{instanceId}")
    public ResponseEntity<Void> deleteInstance(
            @PathVariable Long instanceId,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        appService.deleteInstance(userId, instanceId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/instances/{instanceId}/status")
    public ResponseEntity<AppInstance> updateInstanceStatus(
            @PathVariable Long instanceId,
            @RequestParam String status,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        AppInstance.InstanceStatus instanceStatus = AppInstance.InstanceStatus.valueOf(status.toUpperCase());
        AppInstance instance = appService.updateInstanceStatus(userId, instanceId, instanceStatus);
        return ResponseEntity.ok(instance);
    }
}

