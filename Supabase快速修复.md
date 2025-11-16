# Supabase数据库约束修复

## 问题说明

数据库约束要求小写值（'free', 'vip', 'svip'），但Java代码和插入语句使用大写（'FREE', 'VIP', 'SVIP'），导致约束冲突。

## 快速修复步骤

### 方法1：在Supabase SQL Editor中执行修复脚本

1. 打开Supabase Dashboard
2. 点击左侧 "SQL Editor"
3. 点击 "New query"
4. 复制并执行以下SQL：

```sql
-- 删除旧约束
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_membership_level_check;

-- 添加新约束（支持大写）
ALTER TABLE users ADD CONSTRAINT users_membership_level_check 
    CHECK (membership_level IN ('FREE', 'VIP', 'SVIP'));

-- 更新现有数据（如果有）
UPDATE users SET membership_level = UPPER(membership_level) 
WHERE membership_level IN ('free', 'vip', 'svip');
```

5. 点击 "Run" 执行

### 方法2：重新创建表（如果表是空的）

如果users表还没有数据，可以删除并重新创建：

```sql
-- 删除表（会级联删除相关数据）
DROP TABLE IF EXISTS users CASCADE;

-- 重新创建表（使用正确的约束）
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    membership_level VARCHAR(20) DEFAULT 'FREE' CHECK (membership_level IN ('FREE', 'VIP', 'SVIP')),
    membership_expiry TIMESTAMP,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 重新创建索引
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
```

## 修复后创建测试账号

修复约束后，执行以下SQL创建测试账号：

```sql
-- 基础测试账号（密码：12345678）
INSERT INTO users (username, email, password_hash, membership_level, created_at, updated_at)
VALUES (
    'testuser',
    'test@example.com',
    'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',
    'FREE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- VIP测试账号（密码：12345678）
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

## 验证修复

执行以下查询验证：

```sql
-- 检查约束
SELECT conname, consrc 
FROM pg_constraint 
WHERE conrelid = 'users'::regclass 
AND conname = 'users_membership_level_check';

-- 检查数据
SELECT username, email, membership_level FROM users;
```

## 注意事项

- 修复约束后，所有新的插入必须使用大写值：'FREE', 'VIP', 'SVIP'
- Java代码中的枚举值已经是大写，所以不需要修改
- 如果使用Flyway自动迁移，V3__fix_membership_level_case.sql会自动执行

