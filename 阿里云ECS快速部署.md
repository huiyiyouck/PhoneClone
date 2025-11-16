# 阿里云ECS快速部署指南

## 5分钟快速部署

### 第一步：购买ECS（2分钟）

1. 访问 [阿里云ECS控制台](https://ecs.console.aliyun.com)
2. 点击 "创建实例"
3. 快速配置：
   - **地域**：选择最近的
   - **实例**：ecs.t6-c1m2.large（1核2GB，测试用）
   - **镜像**：Ubuntu 22.04
   - **系统盘**：40GB
   - **网络**：分配公网IP
   - **安全组**：开放8080端口
4. 设置root密码
5. 立即购买

### 第二步：连接服务器（1分钟）

```bash
ssh root@你的ECS公网IP
# 输入密码
```

### 第三步：一键安装脚本（2分钟）

在服务器上执行：

```bash
# 下载并执行安装脚本
curl -fsSL https://raw.githubusercontent.com/your-repo/install.sh -o install.sh
# 或手动执行以下命令
```

#### 手动安装步骤

```bash
# 1. 更新系统
apt update && apt upgrade -y

# 2. 安装Java 21
apt install -y openjdk-21-jdk

# 3. 创建应用目录
mkdir -p /opt/phoneclone
cd /opt/phoneclone

# 4. 上传JAR文件（在本地执行）
# scp backend/build/libs/phoneclone-backend-1.0.0.jar root@你的ECS公网IP:/opt/phoneclone/

# 5. 创建配置文件
cat > application-production.yml <<EOF
spring:
  datasource:
    url: jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
    username: postgres
    password: 你的Supabase密码
  security:
    jwt:
      secret: $(openssl rand -base64 32)
server:
  port: 8080
  servlet:
    context-path: /api
EOF

# 6. 创建systemd服务
cat > /tmp/phoneclone-backend.service <<EOF
[Unit]
Description=PhoneClone Backend
After=network.target

[Service]
Type=simple
ExecStart=/usr/bin/java -jar /opt/phoneclone/phoneclone-backend-1.0.0.jar --spring.config.location=file:/opt/phoneclone/application-production.yml
Restart=always

[Install]
WantedBy=multi-user.target
EOF

sudo mv /tmp/phoneclone-backend.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable phoneclone-backend
sudo systemctl start phoneclone-backend

# 7. 检查状态
sudo systemctl status phoneclone-backend
```

### 第四步：验证部署

```bash
# 测试API
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"12345678"}'
```

## 完整部署脚本

创建 `deploy.sh` 文件：

```bash
#!/bin/bash
# 阿里云ECS部署脚本

set -e

echo "开始部署PhoneClone后端服务..."

# 检查Java
if ! command -v java &> /dev/null; then
    echo "安装Java 21..."
    apt update
    apt install -y openjdk-21-jdk
fi

# 创建目录
mkdir -p /opt/phoneclone
cd /opt/phoneclone

# 提示上传JAR文件
echo "请上传phoneclone-backend-1.0.0.jar到 /opt/phoneclone/"
echo "使用命令: scp build/libs/phoneclone-backend-1.0.0.jar root@你的IP:/opt/phoneclone/"

read -p "JAR文件已上传？(y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

# 创建配置文件
echo "配置环境变量..."
read -p "Supabase数据库URL: " DB_URL
read -p "Supabase用户名: " DB_USER
read -sp "Supabase密码: " DB_PASS
echo
read -sp "JWT密钥（至少32字符，直接回车自动生成）: " JWT_SECRET
echo

if [ -z "$JWT_SECRET" ]; then
    JWT_SECRET=$(openssl rand -base64 32)
    echo "自动生成JWT密钥: $JWT_SECRET"
fi

cat > application-production.yml <<EOF
spring:
  datasource:
    url: $DB_URL
    username: $DB_USER
    password: $DB_PASS
  security:
    jwt:
      secret: $JWT_SECRET
server:
  port: 8080
  servlet:
    context-path: /api
EOF

# 创建systemd服务
cat > /tmp/phoneclone-backend.service <<EOF
[Unit]
Description=PhoneClone Backend Service
After=network.target

[Service]
Type=simple
WorkingDirectory=/opt/phoneclone
ExecStart=/usr/bin/java -Xmx512m -Xms256m -jar /opt/phoneclone/phoneclone-backend-1.0.0.jar --spring.config.location=file:/opt/phoneclone/application-production.yml
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo mv /tmp/phoneclone-backend.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable phoneclone-backend
sudo systemctl start phoneclone-backend

echo "部署完成！"
echo "查看状态: sudo systemctl status phoneclone-backend"
echo "查看日志: sudo journalctl -u phoneclone-backend -f"
```

## 安全组配置

### 必须开放的端口

1. **22** - SSH（远程连接）
2. **8080** - Spring Boot应用（或通过Nginx的80/443）

### 配置步骤

1. ECS控制台 → 实例 → 安全组
2. 点击 "配置规则"
3. 添加规则：
   - **端口**：8080/8080
   - **协议**：TCP
   - **授权对象**：0.0.0.0/0
   - **描述**：Spring Boot API

## 域名和HTTPS配置

### 1. 绑定域名

1. 在阿里云域名控制台解析域名到ECS公网IP
2. 等待DNS生效（通常几分钟）

### 2. 配置Nginx

```bash
# 安装Nginx
apt install -y nginx

# 创建配置
cat > /etc/nginx/sites-available/phoneclone <<EOF
server {
    listen 80;
    server_name your-domain.com;

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

ln -s /etc/nginx/sites-available/phoneclone /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx
```

### 3. 配置SSL

```bash
# 安装Certbot
apt install -y certbot python3-certbot-nginx

# 申请证书
certbot --nginx -d your-domain.com

# 自动续期
certbot renew --dry-run
```

## 常用命令

```bash
# 启动服务
sudo systemctl start phoneclone-backend

# 停止服务
sudo systemctl stop phoneclone-backend

# 重启服务
sudo systemctl restart phoneclone-backend

# 查看状态
sudo systemctl status phoneclone-backend

# 查看日志
sudo journalctl -u phoneclone-backend -f

# 查看最近100行日志
sudo journalctl -u phoneclone-backend -n 100
```

## 更新应用

```bash
# 1. 停止服务
sudo systemctl stop phoneclone-backend

# 2. 备份
cp /opt/phoneclone/phoneclone-backend-1.0.0.jar /opt/phoneclone/phoneclone-backend-1.0.0.jar.bak

# 3. 上传新版本（在本地）
# scp build/libs/phoneclone-backend-1.0.0.jar root@你的IP:/opt/phoneclone/

# 4. 启动服务
sudo systemctl start phoneclone-backend
```

## 故障排查

### 服务无法启动

```bash
# 查看错误日志
sudo journalctl -u phoneclone-backend -n 50 --no-pager

# 检查Java
java -version

# 检查端口
netstat -tulpn | grep 8080
```

### 无法访问

1. 检查安全组是否开放8080端口
2. 检查防火墙：`sudo ufw status`
3. 检查服务状态：`sudo systemctl status phoneclone-backend`

## 完成标志

✅ 服务运行：`sudo systemctl status phoneclone-backend` 显示 active  
✅ API可访问：`curl http://你的IP:8080/api/auth/login` 返回响应  
✅ Swagger UI可访问：浏览器打开 `http://你的IP:8080/swagger-ui.html`  

部署完成！🎉

