-- 修复Supabase数据库约束问题
-- 如果已经执行了V1__init.sql，执行这个脚本来修复约束

-- 1. 删除旧的约束
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_membership_level_check;

-- 2. 添加新的约束（支持大写）
ALTER TABLE users ADD CONSTRAINT users_membership_level_check 
    CHECK (membership_level IN ('FREE', 'VIP', 'SVIP'));

-- 3. 更新现有数据（如果有小写值）
UPDATE users SET membership_level = UPPER(membership_level) 
WHERE membership_level IN ('free', 'vip', 'svip');

-- 4. 现在可以执行测试账号插入语句了
-- 复制 backend/src/main/resources/db/migration/V2__create_test_users.sql 的内容执行

