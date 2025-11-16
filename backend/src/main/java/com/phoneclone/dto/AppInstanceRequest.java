package com.phoneclone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppInstanceRequest {
    @NotBlank(message = "实例名称不能为空")
    private String instanceName;
    
    private String color;
}

