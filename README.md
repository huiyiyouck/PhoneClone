# 多开助手 (PhoneClone)

一个Android应用多开管理系统，支持用户注册登录、应用多开实例管理、数据云端同步等功能。

## 架构方案

采用**混合架构**：
- **Android应用**：客户端 + 轻量级本地服务（仅处理本地数据）
- **Spring Boot后端服务**：处理业务逻辑、认证、数据同步
- **PostgreSQL云数据库**：数据存储（支持多设备同步）

```
┌─────────────────┐
│  Android客户端   │
│  (HTTP API调用)  │
└────────┬────────┘
         │ HTTP/REST
┌────────▼────────┐
│  Spring Boot     │
│  后端API服务     │
└────────┬────────┘
         │ JDBC
┌────────▼────────┐
│  PostgreSQL      │
│  云数据库        │
└─────────────────┘
```

## 项目结构

```
PhoneClone/
├── app/                    # Android客户端
│   ├── src/main/
│   │   ├── java/com/phoneclone/
│   │   │   ├── ui/         # UI界面
│   │   │   ├── viewmodel/  # ViewModel层
│   │   │   ├── repository/ # Repository层
│   │   │   ├── model/      # 数据模型
│   │   │   ├── network/    # 网络请求
│   │   │   ├── database/   # Room数据库（本地缓存）
│   │   │   └── util/       # 工具类
│   │   └── res/            # 资源文件
│   └── build.gradle
├── backend/                # Spring Boot后端
│   ├── src/main/java/com/phoneclone/
│   │   ├── controller/     # REST控制器
│   │   ├── service/        # 业务服务
│   │   ├── repository/     # 数据访问
│   │   ├── entity/         # 实体类
│   │   ├── dto/            # 数据传输对象
│   │   └── security/        # 安全配置
│   └── build.gradle
├── docs/                   # 文档
│   ├── 用户使用文档.md
│   └── 开发文档.md
└── README.md
```

## 快速开始

### 后端启动

1. 配置PostgreSQL数据库
2. 修改`backend/src/main/resources/application.yml`中的数据库连接信息
3. 运行：
```bash
cd backend
./gradlew bootRun
```

### Android客户端

1. 用Android Studio打开项目
2. 修改`app/src/main/java/com/phoneclone/network/RetrofitClient.java`中的API地址
3. 运行应用

详细步骤请查看：
- [快速启动.md](快速启动.md) - 快速启动指南
- [部署手册.md](部署手册.md) - 详细部署说明
- [操作手册.md](操作手册.md) - 用户和管理员操作指南

## 主要功能

- ✅ 用户注册和登录（JWT认证）
- ✅ 应用列表管理
- ✅ 应用多开实例创建和管理
- ✅ 数据云端同步
- ✅ 离线数据缓存

## 技术栈

- **Android**: Java 21, MVVM, Room, Retrofit, Hilt
- **Backend**: Spring Boot 3.2, PostgreSQL, JWT
- **Database**: PostgreSQL

## 测试账号

| 用户名 | 邮箱 | 密码 | 会员等级 |
|--------|------|------|----------|
| testuser | test@example.com | 12345678 | FREE |
| vipuser | vip@example.com | 12345678 | VIP |
| admin | admin@example.com | admin123 | SVIP |

## 文档

- [架构方案评估.md](架构方案评估.md) - 架构方案对比分析
- [部署手册.md](部署手册.md) - 完整部署指南
- [操作手册.md](操作手册.md) - 用户和管理员操作指南
- [快速启动.md](快速启动.md) - 快速启动指南
- [docs/用户使用文档.md](docs/用户使用文档.md) - 用户使用文档
- [docs/开发文档.md](docs/开发文档.md) - 开发文档

## 许可证

MIT License
