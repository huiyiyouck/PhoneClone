package com.phoneclone.controller;

import com.phoneclone.service.DdlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DDL操作控制器
 * 提供数据库DDL操作的API接口
 * 
 * 注意：此接口应该在生产环境中限制访问权限
 */
@RestController
@RequestMapping("/ddl")
public class DdlController {
    
    @Autowired
    private DdlService ddlService;
    
    /**
     * 执行单个DDL语句
     * POST /api/ddl/execute
     * 
     * 请求体示例:
     * {
     *   "sql": "CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(100))"
     * }
     */
    @PostMapping("/execute")
    @PreAuthorize("hasRole('ADMIN')") // 建议添加管理员权限控制
    public ResponseEntity<Map<String, Object>> executeDdl(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            String result = ddlService.executeDdl(sql);
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 批量执行DDL语句
     * POST /api/ddl/execute-batch
     * 
     * 请求体示例:
     * {
     *   "sqlList": [
     *     "CREATE TABLE test1 (id SERIAL PRIMARY KEY)",
     *     "CREATE TABLE test2 (id SERIAL PRIMARY KEY)"
     *   ]
     * }
     */
    @PostMapping("/execute-batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> executeBatchDdl(@RequestBody Map<String, List<String>> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> sqlList = request.get("sqlList");
            if (sqlList == null || sqlList.isEmpty()) {
                response.put("success", false);
                response.put("message", "SQL语句列表不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            String result = ddlService.executeBatchDdl(sqlList);
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 执行查询语句（用于验证DDL结果）
     * POST /api/ddl/query
     */
    @PostMapping("/query")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> executeQuery(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Map<String, Object>> results = ddlService.executeQuery(sql);
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 获取数据库版本信息
     * GET /api/ddl/version
     */
    @GetMapping("/version")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDatabaseVersion() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String version = ddlService.getDatabaseVersion();
            response.put("success", true);
            response.put("version", version);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 获取所有表名
     * GET /api/ddl/tables
     */
    @GetMapping("/tables")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllTables() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> tables = ddlService.getAllTables();
            response.put("success", true);
            response.put("tables", tables);
            response.put("count", tables.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 获取表结构信息
     * GET /api/ddl/table/{tableName}
     */
    @GetMapping("/table/{tableName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTableStructure(@PathVariable String tableName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> structure = ddlService.getTableStructure(tableName);
            response.put("success", true);
            response.put("tableName", tableName);
            response.put("columns", structure);
            response.put("count", structure.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
