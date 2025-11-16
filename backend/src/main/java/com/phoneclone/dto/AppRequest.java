package com.phoneclone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppRequest {
    @NotBlank(message = "包名不能为空")
    private String packageName;
    
    @NotBlank(message = "应用名称不能为空")
    private String appName;
    
    private String iconUrl;
    private String category;
}

