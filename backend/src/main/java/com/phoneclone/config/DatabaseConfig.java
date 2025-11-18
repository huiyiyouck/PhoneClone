package com.phoneclone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * 数据库配置类
 * 支持IPv4和IPv6地址的直接连接
 */
@Configuration
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    
    /**
     * 创建直接JDBC连接的DataSource
     * 用于执行DDL操作
     */
    @Bean(name = "directDataSource")
    public DataSource directDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        // 处理IPv6地址格式
        String jdbcUrl = formatJdbcUrlForIpv6(datasourceUrl);
        
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(datasourceUsername);
        dataSource.setPassword(datasourcePassword);
        
        return dataSource;
    }
    
    /**
     * 创建直接JDBC连接的JdbcTemplate
     * 用于执行原生SQL和DDL操作
     */
    @Bean(name = "directJdbcTemplate")
    public JdbcTemplate directJdbcTemplate() {
        return new JdbcTemplate(directDataSource());
    }
    
    /**
     * 格式化JDBC URL以支持IPv6地址
     * PostgreSQL IPv6地址格式: jdbc:postgresql://[::1]:5432/database
     * 对于域名，如果只支持IPv6，会添加preferIPv6参数
     */
    private String formatJdbcUrlForIpv6(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        
        // 如果URL已经包含方括号，说明已经是IPv6格式，直接返回
        if (url.contains("[") && url.contains("]")) {
            return url;
        }
        
        // 提取主机名或IP地址
        // jdbc:postgresql://host:port/database 格式
        String prefix = "jdbc:postgresql://";
        if (!url.startsWith(prefix)) {
            return url;
        }
        
        String afterPrefix = url.substring(prefix.length());
        int colonIndex = afterPrefix.indexOf(':');
        int slashIndex = afterPrefix.indexOf('/');
        
        if (colonIndex == -1 || slashIndex == -1) {
            return url;
        }
        
        String host = afterPrefix.substring(0, colonIndex);
        String rest = afterPrefix.substring(colonIndex);
        
        // 检查是否是IPv6地址（包含冒号）
        if (host.contains(":") && !host.startsWith("[") && !host.endsWith("]")) {
            // 是IPv6地址，需要添加方括号
            return prefix + "[" + host + "]" + rest;
        }
        
        // 尝试解析是否为IPv6地址
        try {
            // 优先尝试IPv6解析
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress addr : addresses) {
                if (addr instanceof Inet6Address) {
                    // 找到IPv6地址，使用IPv6地址替换域名
                    String ipv6Host = "[" + addr.getHostAddress() + "]";
                    // 检查URL是否已有参数
                    if (rest.contains("?")) {
                        // 已有参数，添加preferIPv6参数
                        return prefix + ipv6Host + rest + "&preferIPv6=true";
                    } else {
                        // 没有参数，添加preferIPv6参数
                        return prefix + ipv6Host + rest + "?preferIPv6=true";
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败，可能是主机名，添加preferIPv6参数强制使用IPv6
            if (rest.contains("?")) {
                return url + "&preferIPv6=true";
            } else {
                return url + "?preferIPv6=true";
            }
        }
        
        // 如果解析到的是IPv4，但需要强制使用IPv6，添加参数
        if (rest.contains("?")) {
            return url + "&preferIPv6=true";
        } else {
            return url + "?preferIPv6=true";
        }
    }
}

