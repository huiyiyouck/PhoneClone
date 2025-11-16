# GitHub上传指南

## 第一步：初始化Git仓库

### 1. 检查Git状态

```bash
cd /Users/chengkang/Project/Project_Android/PhoneClone
git status
```

如果还没有初始化，执行：

```bash
git init
```

### 2. 配置Git用户信息（如果还没有）

```bash
git config --global user.name "你的名字"
git config --global user.email "your-email@example.com"
```

## 第二步：创建GitHub仓库

### 1. 在GitHub上创建仓库

1. 访问 [GitHub](https://github.com)
2. 点击右上角 "+" → "New repository"
3. 填写信息：
   - **Repository name**: `phoneclone`（或你喜欢的名称）
   - **Description**: Android应用多开管理系统
   - **Visibility**: Private（私有）或 Public（公开）
   - **不要**勾选 "Initialize this repository with a README"
4. 点击 "Create repository"

### 2. 获取仓库地址

创建后会显示仓库地址，例如：
```
https://github.com/your-username/phoneclone.git
```

## 第三步：上传代码到GitHub

### 1. 添加远程仓库

```bash
cd /Users/chengkang/Project/Project_Android/PhoneClone

# 添加远程仓库（替换为你的实际仓库地址）
git remote add origin https://github.com/your-username/phoneclone.git

# 或使用SSH（如果配置了SSH密钥）
# git remote add origin git@github.com:your-username/phoneclone.git
```

### 2. 添加文件并提交

```bash
# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: PhoneClone应用多开系统"

# 推送到GitHub
git branch -M main
git push -u origin main
```

### 3. 如果遇到认证问题

**使用Personal Access Token**：

1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. 点击 "Generate new token"
3. 选择权限：`repo`（完整仓库访问权限）
4. 生成并复制token
5. 推送时使用token作为密码：

```bash
git push -u origin main
# Username: 你的GitHub用户名
# Password: 粘贴你的token
```

## 第四步：验证上传

1. 访问你的GitHub仓库页面
2. 确认所有文件都已上传
3. 检查 `.gitignore` 是否正确排除了不需要的文件

## 后续更新代码

```bash
# 1. 修改代码后
git add .
git commit -m "描述你的更改"
git push origin main
```

## 重要文件说明

已配置 `.gitignore` 排除：
- 构建文件（build/, *.jar, *.apk）
- IDE配置文件（.idea/, *.iml）
- 敏感配置文件（application-production.yml）
- 临时文件

**注意**：`application-production.yml` 包含敏感信息（数据库密码、JWT密钥），不会上传到GitHub。

## 保护敏感信息

### 使用环境变量（推荐）

在GitHub仓库中创建 `backend/.env.example`：

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
```

实际配置在服务器上创建，不提交到GitHub。

