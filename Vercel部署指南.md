# Vercel部署指南

## 重要说明

**Vercel主要支持Node.js和Serverless Functions**，对于Spring Boot Java应用，有以下方案：

### 方案A：使用Vercel Serverless Functions（推荐用于小规模）

将Spring Boot应用转换为Serverless Functions，但需要重构代码。

### 方案B：使用其他平台（推荐）

对于Spring Boot应用，建议使用以下平台：
- **Railway** - 支持Java，部署简单
- **Render** - 免费套餐，支持Java
- **Fly.io** - 支持Docker，性能好
- **Heroku** - 经典选择（需付费）

### 方案C：使用Vercel + Docker（如果支持）

如果Vercel支持Docker，可以使用Dockerfile部署。

## 方案B推荐：使用Railway部署（最简单）

### 第一步：准备部署

1. **安装Railway CLI**（可选，也可以使用Web界面）

```bash
# macOS
brew install railway

# 或使用npm
npm i -g @railway/cli
```

2. **登录Railway**

```bash
railway login
```

### 第二步：部署到Railway

#### 方法1：使用Railway Web界面

1. 访问 [https://railway.app](https://railway.app)
2. 使用GitHub账号登录
3. 点击 "New Project"
4. 选择 "Deploy from GitHub repo"
5. 选择你的项目仓库
6. 选择 `backend` 目录作为根目录
7. Railway会自动检测Java项目并部署

#### 方法2：使用Railway CLI

```bash
cd backend
railway init
railway up
```

### 第三步：配置环境变量

在Railway Dashboard中：

1. 进入项目 → 选择服务
2. 点击 "Variables" 标签
3. 添加以下环境变量：

```
DATABASE_URL=jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?user=postgres&password=YOUR-PASSWORD&sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=YOUR-SUPABASE-PASSWORD
JWT_SECRET=your-256-bit-secret-key-change-this-in-production
SPRING_PROFILES_ACTIVE=production
PORT=8080
```

### 第四步：获取部署URL

部署完成后，Railway会提供一个URL，例如：
```
https://your-app.railway.app
```

## 方案C：使用Render部署（免费选项）

### 第一步：准备部署

1. 访问 [https://render.com](https://render.com)
2. 使用GitHub账号登录

### 第二步：创建Web Service

1. 点击 "New" → "Web Service"
2. 连接GitHub仓库
3. 配置：
   - **Name**: `phoneclone-backend`
   - **Root Directory**: `backend`
   - **Environment**: `Java`
   - **Build Command**: `./gradlew build -x test`
   - **Start Command**: `java -jar build/libs/phoneclone-backend-1.0.0.jar`

### 第三步：配置环境变量

在Render Dashboard中：

1. 进入服务 → "Environment"
2. 添加环境变量（同Railway配置）

### 第四步：部署

点击 "Save Changes"，Render会自动部署。

## 如果必须使用Vercel

### 方案：将Spring Boot转换为Serverless Functions

这需要重构代码，将Controller转换为Vercel Serverless Functions。

创建 `api/` 目录结构：

```
api/
├── auth/
│   ├── register.js
│   └── login.js
└── apps/
    └── index.js
```

但这种方式需要大量重构，**不推荐**。

## 推荐方案总结

**最佳选择：Railway**
- ✅ 支持Java/Spring Boot原生
- ✅ 部署简单
- ✅ 有免费套餐
- ✅ 自动HTTPS
- ✅ 支持环境变量
- ✅ 自动部署（GitHub集成）

**备选：Render**
- ✅ 免费套餐
- ✅ 支持Java
- ✅ 简单配置

## 部署后配置

### 1. 更新Android应用API地址

编辑 `app/src/main/java/com/phoneclone/network/RetrofitClient.java`:

```java
// 生产环境
private static final String BASE_URL = "https://your-app.railway.app/api/";
// 或
// private static final String BASE_URL = "https://your-app.onrender.com/api/";
```

### 2. 测试API

```bash
# 测试注册接口
curl -X POST https://your-app.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"12345678"}'
```

### 3. 访问Swagger UI

```
https://your-app.railway.app/swagger-ui.html
```

## 监控和维护

### Railway

- Dashboard: https://railway.app/dashboard
- 查看日志：在服务页面点击 "View Logs"
- 查看指标：在服务页面查看 "Metrics"

### Render

- Dashboard: https://dashboard.render.com
- 查看日志：在服务页面点击 "Logs"
- 查看指标：在服务页面查看 "Metrics"

## 故障排查

### 部署失败

1. 检查构建日志
2. 确认环境变量配置正确
3. 检查数据库连接
4. 查看应用日志

### 应用无法启动

1. 检查端口配置（Railway/Render会自动分配端口）
2. 检查环境变量
3. 查看启动日志

### 数据库连接失败

1. 确认Supabase连接信息正确
2. 检查SSL配置
3. 确认防火墙规则

