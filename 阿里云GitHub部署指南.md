# 阿里云GitHub部署指南

## 部署流程

```
本地开发 → GitHub → 阿里云ECS自动拉取 → 自动部署
```

## 第一步：上传代码到GitHub

### 1. 初始化Git仓库

```bash
cd /Users/chengkang/Project/Project_Android/PhoneClone

# 如果还没有初始化
git init

# 配置用户信息（如果还没有）
git config --global user.name "你的名字"
git config --global user.email "your-email@example.com"
```

### 2. 创建GitHub仓库

1. 访问 [GitHub](https://github.com)
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
- 生成Token：GitHub → Settings → Developer settings → Personal access tokens

## 第二步：在阿里云ECS上配置

### 1. 首次部署设置

SSH连接到ECS服务器：

```bash
ssh root@你的ECS公网IP
```

### 2. 安装必要工具

```bash
# 更新系统
apt update && apt upgrade -y

# 安装Java 21
apt install -y openjdk-21-jdk

# 安装Git
apt install -y git

# 验证安装
java -version
git --version
```

### 3. 配置Git（如果需要）

```bash
git config --global user.name "Deploy User"
git config --global user.email "deploy@phoneclone.com"
```

### 4. 下载部署脚本

```bash
# 创建部署目录
mkdir -p /opt/phoneclone
cd /opt/phoneclone

# 下载部署脚本（从GitHub或手动创建）
# 方法1：如果脚本已上传到GitHub
git clone https://github.com/your-username/phoneclone.git /tmp/phoneclone
cp /tmp/phoneclone/阿里云自动部署脚本.sh /opt/phoneclone/deploy.sh
chmod +x /opt/phoneclone/deploy.sh

# 方法2：手动创建（见下一步）
```

### 5. 创建部署脚本

如果脚本不在GitHub上，手动创建：

```bash
nano /opt/phoneclone/deploy.sh
```

复制 `阿里云自动部署脚本.sh` 的内容，并修改：

```bash
# 修改这行为你的GitHub仓库地址
GITHUB_REPO="https://github.com/your-username/phoneclone.git"
```

保存并设置执行权限：

```bash
chmod +x /opt/phoneclone/deploy.sh
```

### 6. 首次部署

```bash
# 运行部署脚本
/opt/phoneclone/deploy.sh
```

脚本会：
1. 从GitHub克隆代码
2. 构建项目
3. 检查配置文件
4. 创建systemd服务
5. 启动服务

### 7. 创建配置文件

首次部署会提示创建配置文件：

```bash
nano /opt/phoneclone/backend/application-production.yml
```

填入Supabase数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
    username: postgres
    password: 你的Supabase密码
  security:
    jwt:
      secret: 你的JWT密钥（至少32字符，使用 openssl rand -base64 32 生成）
server:
  port: 8080
  servlet:
    context-path: /api
```

保存后再次运行部署脚本：

```bash
/opt/phoneclone/deploy.sh
```

## 第三步：后续更新部署

### 方法1：手动触发部署

在ECS服务器上执行：

```bash
/opt/phoneclone/deploy.sh
```

### 方法2：设置定时自动部署（可选）

```bash
# 编辑crontab
crontab -e

# 添加定时任务（每天凌晨2点自动部署）
0 2 * * * /opt/phoneclone/deploy.sh >> /var/log/phoneclone-deploy.log 2>&1
```

### 方法3：使用GitHub Webhook自动部署（高级）

需要配置Webhook服务器，这里不详细展开。

## 部署脚本说明

`阿里云自动部署脚本.sh` 会自动执行：

1. ✅ 检查Java和Git
2. ✅ 从GitHub拉取最新代码
3. ✅ 构建项目（`./gradlew build`）
4. ✅ 检查配置文件
5. ✅ 停止旧服务
6. ✅ 备份旧版本
7. ✅ 启动新服务
8. ✅ 验证服务状态

## 验证部署

### 1. 检查服务状态

```bash
systemctl status phoneclone-backend
```

### 2. 查看日志

```bash
journalctl -u phoneclone-backend -f
```

### 3. 测试API

```bash
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"12345678"}'
```

### 4. 访问Swagger UI

```
http://你的ECS公网IP:8080/swagger-ui.html
```

## 常用命令

### 更新代码并部署

```bash
# 在ECS上执行
/opt/phoneclone/deploy.sh
```

### 查看服务日志

```bash
# 实时日志
journalctl -u phoneclone-backend -f

# 最近100行
journalctl -u phoneclone-backend -n 100
```

### 重启服务

```bash
systemctl restart phoneclone-backend
```

### 回滚到上一个版本

```bash
# 停止服务
systemctl stop phoneclone-backend

# 恢复备份（找到最新的备份目录）
cp /opt/phoneclone/backups/YYYYMMDD_HHMMSS/phoneclone-backend-1.0.0.jar \
   /opt/phoneclone/backend/build/libs/

# 启动服务
systemctl start phoneclone-backend
```

## 安全建议

### 1. 使用SSH密钥认证GitHub

```bash
# 生成SSH密钥
ssh-keygen -t ed25519 -C "deploy@phoneclone"

# 复制公钥到GitHub
cat ~/.ssh/id_ed25519.pub
# 在GitHub → Settings → SSH and GPG keys 中添加

# 使用SSH URL
git remote set-url origin git@github.com:your-username/phoneclone.git
```

### 2. 保护配置文件

`application-production.yml` 包含敏感信息，**不要**提交到GitHub。

在 `.gitignore` 中已排除：
```
application-production.yml
```

### 3. 使用环境变量（更安全）

在服务器上设置环境变量，而不是配置文件：

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://..."
export DB_PASSWORD="..."
export JWT_SECRET="..."
```

## 故障排查

### 问题1：Git克隆失败

**检查**：
- GitHub仓库地址是否正确
- 网络连接是否正常
- 认证信息是否正确

### 问题2：构建失败

**检查**：
- Java版本是否正确
- 查看构建日志
- 检查依赖是否完整

### 问题3：服务启动失败

**检查**：
- 配置文件是否正确
- 数据库连接是否正常
- 查看服务日志：`journalctl -u phoneclone-backend -n 100`

## 完成标志

✅ 代码已上传到GitHub  
✅ ECS可以成功拉取代码  
✅ 自动部署脚本运行正常  
✅ 服务正常运行  
✅ API可以访问  

**部署流程建立完成！** 🎉

