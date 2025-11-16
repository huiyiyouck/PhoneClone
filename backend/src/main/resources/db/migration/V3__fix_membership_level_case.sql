-- 修复membership_level约束，允许大写值（与Java枚举一致）
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_membership_level_check;
ALTER TABLE users ADD CONSTRAINT users_membership_level_check 
    CHECK (membership_level IN ('FREE', 'VIP', 'SVIP'));

-- 更新现有数据（如果有小写值）
UPDATE users SET membership_level = UPPER(membership_level) 
WHERE membership_level IN ('free', 'vip', 'svip');

