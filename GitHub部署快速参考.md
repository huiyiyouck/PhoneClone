# GitHub部署快速参考

## 📋 部署流程概览

```
本地代码 → GitHub → 阿里云ECS自动拉取 → 自动部署
```

## 🚀 第一步：上传代码到GitHub

### 1. 初始化Git（如果还没有）

```bash
cd /Users/chengkang/Project/Project_Android/PhoneClone
git init
git config --global user.name "你的名字"
git config --global user.email "your-email@example.com"
```

### 2. 创建GitHub仓库

1. 访问 https://github.com
2. 点击 "New repository"
3. 填写仓库名称（如：`phoneclone`）
4. 选择 Private 或 Public
5. **不要**勾选 "Initialize with README"
6. 点击 "Create repository"

### 3. 添加远程仓库并推送

```bash
# 添加远程仓库（替换为你的实际地址）
git remote add origin https://github.com/your-username/phoneclone.git

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: PhoneClone应用多开系统"

# 推送到GitHub
git branch -M main
git push -u origin main
```

**如果遇到认证问题**：
- 使用Personal Access Token作为密码
- 生成Token：GitHub → Settings → Developer settings → Personal access tokens → Generate new token (classic)
- 选择 `repo` 权限

## 🖥️ 第二步：在阿里云ECS上部署

### 1. SSH连接到ECS

```bash
ssh root@你的ECS公网IP
```

### 2. 安装必要工具

```bash
apt update && apt upgrade -y
apt install -y openjdk-21-jdk git
```

### 3. 下载部署脚本

```bash
# 创建部署目录
mkdir -p /opt/phoneclone
cd /opt/phoneclone

# 从GitHub克隆（首次）
git clone https://github.com/your-username/phoneclone.git /tmp/phoneclone
cp /tmp/phoneclone/阿里云自动部署脚本.sh /opt/phoneclone/deploy.sh
chmod +x /opt/phoneclone/deploy.sh

# 编辑脚本，修改GitHub仓库地址
nano /opt/phoneclone/deploy.sh
# 找到这一行并修改：
# GITHUB_REPO="https://github.com/your-username/phoneclone.git"
```

### 4. 首次部署

```bash
/opt/phoneclone/deploy.sh
```

### 5. 创建配置文件

首次部署会提示创建配置文件：

```bash
nano /opt/phoneclone/backend/application-production.yml
```

填入配置：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
    username: postgres
    password: 你的Supabase密码
  security:
    jwt:
      secret: 你的JWT密钥（使用 openssl rand -base64 32 生成）
server:
  port: 8080
  servlet:
    context-path: /api
```

保存后再次运行：

```bash
/opt/phoneclone/deploy.sh
```

## 🔄 后续更新

### 本地更新代码后

```bash
# 在本地
git add .
git commit -m "描述你的更改"
git push origin main
```

### 在ECS上部署更新

```bash
# 在ECS上
/opt/phoneclone/deploy.sh
```

脚本会自动：
1. 从GitHub拉取最新代码
2. 构建项目
3. 停止旧服务
4. 启动新服务

## ✅ 验证部署

```bash
# 检查服务状态
systemctl status phoneclone-backend

# 查看日志
journalctl -u phoneclone-backend -f

# 测试API
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"12345678"}'
```

## 📝 常用命令

```bash
# 查看服务状态
systemctl status phoneclone-backend

# 重启服务
systemctl restart phoneclone-backend

# 查看日志
journalctl -u phoneclone-backend -f
journalctl -u phoneclone-backend -n 100

# 停止服务
systemctl stop phoneclone-backend

# 启动服务
systemctl start phoneclone-backend
```

## 🔒 安全提示

1. **不要**将 `application-production.yml` 提交到GitHub（已在`.gitignore`中排除）
2. 使用SSH密钥认证GitHub（可选但推荐）
3. 定期更新JWT密钥和数据库密码

## 📚 详细文档

- **GitHub上传指南.md** - 详细的上传步骤
- **阿里云GitHub部署指南.md** - 完整的部署文档
- **阿里云自动部署脚本.sh** - 自动部署脚本

## ❓ 故障排查

### 问题：Git推送失败

**解决**：
- 检查网络连接
- 使用Personal Access Token
- 确认仓库地址正确

### 问题：部署脚本执行失败

**解决**：
- 检查Java和Git是否安装
- 查看脚本输出错误信息
- 确认GitHub仓库地址正确
- 检查配置文件是否存在

### 问题：服务启动失败

**解决**：
```bash
# 查看详细日志
journalctl -u phoneclone-backend -n 100

# 检查配置文件
cat /opt/phoneclone/backend/application-production.yml

# 检查数据库连接
# 确认Supabase连接信息正确
```

---

**完成！** 🎉 现在你可以通过GitHub管理代码，并在阿里云ECS上自动部署了。

