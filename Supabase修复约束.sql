-- Supabase数据库约束修复脚本
-- 在Supabase SQL Editor中执行此脚本

-- 步骤1: 删除旧的约束
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_membership_level_check;

-- 步骤2: 添加新的约束（支持大写：FREE, VIP, SVIP）
ALTER TABLE users ADD CONSTRAINT users_membership_level_check 
    CHECK (membership_level IN ('FREE', 'VIP', 'SVIP'));

-- 步骤3: 更新现有数据（如果有小写值，转换为大写）
UPDATE users 
SET membership_level = UPPER(membership_level) 
WHERE membership_level IN ('free', 'vip', 'svip');

-- 验证约束已更新
SELECT 
    conname as constraint_name,
    pg_get_constraintdef(oid) as constraint_definition
FROM pg_constraint 
WHERE conrelid = 'users'::regclass 
AND conname = 'users_membership_level_check';

-- 现在可以执行测试账号插入语句了

