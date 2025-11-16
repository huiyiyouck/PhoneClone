# Supabase数据库部署指南

## 第一步：创建Supabase项目

### 1. 注册Supabase账号

1. 访问 [https://supabase.com](https://supabase.com)
2. 点击 "Start your project"
3. 使用GitHub账号登录（推荐）或邮箱注册

### 2. 创建新项目

1. 登录后点击 "New Project"
2. 填写项目信息：
   - **Name**: `phoneclone`（或你喜欢的名称）
   - **Database Password**: 设置一个强密码（**重要：保存好这个密码**）
   - **Region**: 选择离你最近的区域（如：Southeast Asia (Singapore)）
3. 点击 "Create new project"
4. 等待项目创建完成（约2-3分钟）

### 3. 获取数据库连接信息

项目创建完成后：

1. 进入项目 Dashboard
2. 点击左侧菜单 "Settings" → "Database"
3. 找到 "Connection string" 部分
4. 选择 "URI" 标签
5. 复制连接字符串，格式如下：
   ```
   postgresql://postgres:[YOUR-PASSWORD]@db.xxxxx.supabase.co:5432/postgres
   ```

**重要信息**：
- **Host**: `db.xxxxx.supabase.co`
- **Port**: `5432`
- **Database**: `postgres`
- **User**: `postgres`
- **Password**: 你创建项目时设置的密码

## 第二步：配置数据库连接池

### 1. 修改连接字符串格式

Supabase的连接字符串需要转换为JDBC格式：

```
jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?user=postgres&password=YOUR-PASSWORD
```

### 2. 配置连接池参数

在Supabase Dashboard中：
1. 进入 "Settings" → "Database"
2. 找到 "Connection Pooling" 部分
3. 启用连接池（推荐使用 "Transaction" 模式）
4. 使用连接池的连接字符串（端口通常是6543）

## 第三步：初始化数据库表结构

### 方法1：使用Supabase SQL Editor（推荐）

1. 在Supabase Dashboard中，点击左侧 "SQL Editor"
2. 点击 "New query"
3. 复制 `backend/src/main/resources/db/migration/V1__init.sql` 的内容
4. 粘贴到SQL Editor中
5. 点击 "Run" 执行SQL

### 方法2：使用psql命令行

```bash
# 安装PostgreSQL客户端（如果还没有）
# macOS: brew install postgresql
# Ubuntu: sudo apt-get install postgresql-client

# 连接到Supabase数据库
psql "postgresql://postgres:[YOUR-PASSWORD]@db.xxxxx.supabase.co:5432/postgres"

# 执行SQL脚本
\i backend/src/main/resources/db/migration/V1__init.sql
```

### 方法3：通过Spring Boot自动迁移

配置好数据库连接后，Spring Boot启动时会自动执行Flyway迁移。

## 第四步：创建测试账号

在Supabase SQL Editor中执行：

```sql
-- 创建测试账号（密码：12345678，SHA-256哈希）
INSERT INTO users (username, email, password_hash, membership_level, created_at, updated_at)
VALUES (
    'testuser',
    'test@example.com',
    'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',
    'FREE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- VIP测试账号
INSERT INTO users (username, email, password_hash, membership_level, membership_expiry, created_at, updated_at)
VALUES (
    'vipuser',
    'vip@example.com',
    'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',
    'VIP',
    CURRENT_TIMESTAMP + INTERVAL '1 month',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- 管理员账号（密码：admin123）
INSERT INTO users (username, email, password_hash, membership_level, created_at, updated_at)
VALUES (
    'admin',
    'admin@example.com',
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
    'SVIP',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;
```

## 第五步：配置环境变量

在Supabase Dashboard中：

1. 进入 "Settings" → "API"
2. 记录以下信息（用于后续配置）：
   - Project URL
   - anon/public key
   - service_role key（**保密，不要暴露**）

## 安全建议

### 1. 数据库安全

- ✅ 使用强密码
- ✅ 启用SSL连接（Supabase默认启用）
- ✅ 定期备份（Supabase自动备份）
- ✅ 使用连接池（减少连接数）

### 2. 连接字符串安全

- ✅ 不要在代码中硬编码密码
- ✅ 使用环境变量存储敏感信息
- ✅ 使用Supabase的连接池功能

## 验证部署

### 测试数据库连接

```bash
# 使用psql测试连接
psql "postgresql://postgres:[YOUR-PASSWORD]@db.xxxxx.supabase.co:5432/postgres"

# 执行测试查询
SELECT COUNT(*) FROM users;
```

### 在应用中测试

配置好数据库连接后，启动Spring Boot应用，检查日志确认连接成功。

## 常见问题

### Q1: 连接超时

**解决**：
- 检查防火墙设置
- 确认使用正确的端口（5432或连接池端口6543）
- 检查网络连接

### Q2: 认证失败

**解决**：
- 确认密码正确
- 检查用户名是否为 `postgres`
- 确认数据库名称正确

### Q3: SSL连接问题

**解决**：
- Supabase默认要求SSL连接
- 在JDBC URL中添加：`?sslmode=require`
- 或使用：`?sslmode=verify-full`（更安全）

## 下一步

数据库配置完成后，继续配置Vercel部署后端服务。

