-- 创建测试账号
-- 密码都是：12345678 (SHA-256: ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f)

-- 基础测试账号
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

