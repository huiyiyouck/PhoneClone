# Gradle手动安装指南

## 📥 手动下载Gradle

### 下载地址

Gradle 8.5 下载链接：
```
https://services.gradle.org/distributions/gradle-8.5-bin.zip
```

### 上传目录

Gradle wrapper 会将下载的Gradle解压到以下目录：

```bash
# 对于root用户
/root/.gradle/wrapper/dists/gradle-8.5-bin/[哈希值]/gradle-8.5-bin.zip
```

**但是**，更简单的方法是：

#### 方法1：上传到临时目录，让wrapper自动处理（推荐）

```bash
# 1. 上传到服务器临时目录
# 例如：/tmp/gradle-8.5-bin.zip

# 2. 创建Gradle缓存目录结构
mkdir -p /root/.gradle/wrapper/dists/gradle-8.5-bin

# 3. 计算哈希值（wrapper会自动创建）
# 或者直接让wrapper下载一次，然后替换文件
```

#### 方法2：直接放到wrapper的缓存目录（最简单）

```bash
# 1. 先运行一次gradlew（会创建目录结构）
cd /opt/phoneclone/backend
./gradlew --version

# 2. 这会创建目录，例如：
# /root/.gradle/wrapper/dists/gradle-8.5-bin/[哈希值]/

# 3. 查看创建的目录
ls -la /root/.gradle/wrapper/dists/gradle-8.5-bin/

# 4. 上传你的 gradle-8.5-bin.zip 到该目录下的子目录
# 例如：/root/.gradle/wrapper/dists/gradle-8.5-bin/[哈希值]/gradle-8.5-bin.zip
```

## 🚀 完整操作步骤

### 步骤1：在服务器上触发wrapper创建目录

```bash
cd /opt/phoneclone/backend

# 运行gradlew，让它开始下载（会创建目录结构）
# 可以按 Ctrl+C 中断下载
./gradlew --version
```

### 步骤2：查看创建的目录

```bash
# 查看wrapper创建的目录
ls -la /root/.gradle/wrapper/dists/gradle-8.5-bin/

# 会看到一个以哈希值命名的目录，例如：
# drwxr-xr-x 2 root root 4096 ... a1b2c3d4e5f6...
```

### 步骤3：上传文件

将你下载的 `gradle-8.5-bin.zip` 上传到：

```bash
# 上传到该目录
/root/.gradle/wrapper/dists/gradle-8.5-bin/[哈希值目录]/gradle-8.5-bin.zip

# 例如：
/root/.gradle/wrapper/dists/gradle-8.5-bin/a1b2c3d4e5f6.../gradle-8.5-bin.zip
```

### 步骤4：验证

```bash
# 再次运行gradlew，应该会直接使用上传的文件
cd /opt/phoneclone/backend
./gradlew --version
```

## 📋 使用scp上传（从本地到服务器）

```bash
# 在本地执行（替换为你的服务器IP和实际路径）
scp gradle-8.5-bin.zip root@你的服务器IP:/root/.gradle/wrapper/dists/gradle-8.5-bin/[哈希值目录]/

# 例如：
scp gradle-8.5-bin.zip root@47.xxx.xxx.xxx:/root/.gradle/wrapper/dists/gradle-8.5-bin/a1b2c3d4e5f6.../
```

## 🔍 查找哈希值目录的快速方法

```bash
# 方法1：运行gradlew，查看输出
cd /opt/phoneclone/backend
./gradlew --version 2>&1 | grep -i "gradle-8.5"

# 方法2：查看wrapper日志
cat /root/.gradle/wrapper/dists/gradle-8.5-bin/*/gradle-8.5-bin.zip.lck 2>/dev/null

# 方法3：列出所有目录
ls -la /root/.gradle/wrapper/dists/gradle-8.5-bin/
```

## ⚡ 更简单的方法：使用系统Gradle

如果服务器上已经安装了Gradle，可以直接使用：

```bash
# 安装Gradle
apt update
apt install -y gradle

# 验证
gradle --version

# 然后使用系统Gradle初始化wrapper
cd /opt/phoneclone/backend
gradle wrapper --gradle-version 8.5
```

## 🎯 推荐方案

### 方案A：让wrapper创建目录后上传（最简单）

```bash
# 1. 在服务器上运行一次（会创建目录，可以中断）
cd /opt/phoneclone/backend
timeout 5 ./gradlew --version || true

# 2. 查看创建的目录
ls -la /root/.gradle/wrapper/dists/gradle-8.5-bin/

# 3. 上传文件到该目录下的子目录
# 使用scp或sftp上传 gradle-8.5-bin.zip

# 4. 再次运行gradlew
./gradlew --version
```

### 方案B：直接安装系统Gradle（最快）

```bash
# 安装Gradle
apt update
apt install -y gradle

# 使用系统Gradle
cd /opt/phoneclone/backend
gradle wrapper --gradle-version 8.5
./gradlew build
```

## 📝 完整上传命令示例

假设哈希值目录是 `a1b2c3d4e5f6...`：

```bash
# 在本地执行
scp gradle-8.5-bin.zip root@你的服务器IP:/root/.gradle/wrapper/dists/gradle-8.5-bin/a1b2c3d4e5f6.../gradle-8.5-bin.zip

# 在服务器上验证
ls -lh /root/.gradle/wrapper/dists/gradle-8.5-bin/a1b2c3d4e5f6.../gradle-8.5-bin.zip
```

## ✅ 验证安装

```bash
cd /opt/phoneclone/backend
./gradlew --version

# 应该看到：
# Gradle 8.5
# ...
```

---

**总结**：上传目录是 `/root/.gradle/wrapper/dists/gradle-8.5-bin/[哈希值目录]/gradle-8.5-bin.zip`

**最简单的方法**：先运行一次 `./gradlew --version` 创建目录，然后上传文件到该目录。

