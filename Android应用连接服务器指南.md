# Android应用连接服务器指南

## 🎯 目标

配置Android应用连接到已部署的阿里云后端服务。

## 📋 第一步：获取服务器地址

### 1. 获取服务器公网IP

在阿里云ECS服务器上执行：

```bash
# 方法1：查看公网IP
curl ifconfig.me

# 方法2：查看服务器信息
hostname -I

# 方法3：在阿里云控制台查看
# ECS控制台 → 实例 → 网络和安全组 → 公网IP
```

### 2. 确认服务端口

根据部署配置，服务运行在：
- **端口**：8080
- **Context Path**：/api
- **完整地址**：`http://你的公网IP:8080/api/`

## 🔧 第二步：修改Android应用配置

### 1. 修改API地址

编辑 `app/src/main/java/com/phoneclone/network/RetrofitClient.java`：

```java
// 将这一行：
private static final String BASE_URL = "https://YOUR_VERCEL_PROJECT.vercel.app/api/";

// 改为你的阿里云服务器地址：
private static final String BASE_URL = "http://你的公网IP:8080/api/";
// 例如：http://47.xxx.xxx.xxx:8080/api/
```

**注意**：
- 如果配置了域名，使用域名：`http://your-domain.com:8080/api/`
- 如果配置了HTTPS，使用：`https://your-domain.com/api/`（端口443可省略）

### 2. 允许HTTP连接（如果需要）

如果使用HTTP（非HTTPS），确保 `AndroidManifest.xml` 中已配置：

```xml
<application
    android:usesCleartextTraffic="true">
```

（已在配置文件中）

## 🏗️ 第三步：构建APK

### 方法1：使用Android Studio（推荐）

1. **打开项目**：
   - 在Android Studio中打开项目

2. **同步项目**：
   - 点击 "Sync Project with Gradle Files"
   - 等待同步完成

3. **构建APK**：
   - 菜单：**Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
   - 或使用快捷键：`Ctrl+Shift+A`（Mac: `Cmd+Shift+A`）→ 输入 "Build APK"

4. **APK位置**：
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### 方法2：使用命令行

```bash
# 进入项目目录
cd /Users/chengkang/Project/Project_Android/PhoneClone

# 构建Debug APK
./gradlew assembleDebug

# 或构建Release APK（需要签名配置）
./gradlew assembleRelease

# APK位置：
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release.apk
```

## 📱 第四步：安装APK

### 方法1：通过USB安装（真机）

1. **启用USB调试**：
   - 手机设置 → 关于手机 → 连续点击"版本号"7次（启用开发者选项）
   - 设置 → 开发者选项 → 启用"USB调试"

2. **连接手机**：
   ```bash
   # 检查设备连接
   adb devices
   
   # 安装APK
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### 方法2：直接传输安装

1. **传输APK到手机**：
   - 通过USB、微信、QQ等方式将APK文件传输到手机

2. **在手机上安装**：
   - 打开文件管理器
   - 找到APK文件
   - 点击安装（需要允许"安装未知来源应用"）

### 方法3：使用Android Studio直接运行

1. **连接设备或启动模拟器**
2. **点击运行按钮**（绿色三角形）
3. **选择设备**
4. **应用会自动安装并启动**

## ✅ 第五步：测试连接

### 1. 测试后端服务（在服务器上）

```bash
# SSH连接到服务器
ssh root@你的服务器IP

# 测试API是否可访问
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"12345678"}'

# 或从外部测试（使用公网IP）
curl http://你的公网IP:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"12345678"}'
```

### 2. 检查防火墙规则

确保阿里云安全组开放了8080端口：

1. **阿里云控制台**：
   - ECS → 实例 → 安全组 → 配置规则

2. **添加入站规则**：
   - 端口：8080
   - 协议：TCP
   - 授权对象：0.0.0.0/0（或限制为特定IP）

3. **检查服务器防火墙**：

```bash
# 检查防火墙状态
ufw status

# 如果启用了防火墙，开放8080端口
ufw allow 8080/tcp
```

### 3. 在Android应用中测试

1. **打开应用**
2. **使用测试账号登录**：
   - 邮箱：`test@example.com`
   - 密码：`12345678`

3. **检查功能**：
   - 登录是否成功
   - 是否能获取应用列表
   - 是否能创建多开实例

## 🔍 故障排查

### 问题1：无法连接到服务器

**检查清单**：
- ✅ API地址是否正确（包含 `/api/` 后缀）
- ✅ 服务器公网IP是否正确
- ✅ 端口8080是否开放
- ✅ 服务器防火墙是否允许8080端口
- ✅ 阿里云安全组是否配置正确

**测试命令**：
```bash
# 在本地测试服务器连接
curl http://你的公网IP:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"12345678"}'
```

### 问题2：网络超时

**可能原因**：
- 服务器未启动
- 防火墙阻止
- 网络问题

**解决**：
```bash
# 检查服务状态
systemctl status phoneclone-backend

# 查看服务日志
journalctl -u phoneclone-backend -n 50
```

### 问题3：SSL/TLS错误

**如果使用HTTPS**，可能需要：
1. 配置SSL证书
2. 在Android应用中配置证书固定
3. 或暂时使用HTTP测试

## 📝 测试账号

| 用户名 | 邮箱 | 密码 | 会员等级 |
|--------|------|------|----------|
| testuser | test@example.com | 12345678 | FREE |
| vipuser | vip@example.com | 12345678 | VIP |
| admin | admin@example.com | admin123 | SVIP |

## 🚀 快速操作清单

- [ ] 获取服务器公网IP
- [ ] 修改 `RetrofitClient.java` 中的 `BASE_URL`
- [ ] 构建APK（Debug或Release）
- [ ] 检查防火墙和安全组配置
- [ ] 安装APK到设备
- [ ] 测试登录功能
- [ ] 验证API连接

## 🔒 安全建议

### 1. 使用HTTPS（生产环境）

配置Nginx反向代理和SSL证书：

```nginx
server {
    listen 443 ssl;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

### 2. 限制访问IP（可选）

在阿里云安全组中限制只允许特定IP访问。

### 3. 使用域名（推荐）

配置域名解析到服务器IP，使用域名访问更专业。

---

**完成！** 现在你的Android应用应该可以连接到阿里云服务器了。

