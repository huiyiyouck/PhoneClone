# SSH密钥配置完整指南 - 解决Permission denied问题

## 🔴 你遇到的错误

```
git@github.com: Permission denied (publickey).
fatal: Could not read from remote repository.
```

**原因**：ECS服务器上没有SSH密钥，或者SSH密钥没有添加到GitHub账户。

## ✅ 完整解决步骤

### 第一步：检查是否已有SSH密钥

在ECS服务器上执行：

```bash
# 查看.ssh目录
ls -la ~/.ssh/
```

**如果看到** `id_rsa` 和 `id_rsa.pub`（或 `id_ed25519` 和 `id_ed25519.pub`）：
- 跳到 **第三步：查看公钥**

**如果没有看到**：
- 继续 **第二步：生成SSH密钥**

### 第二步：生成SSH密钥

```bash
# 生成ED25519密钥（推荐，更安全）
ssh-keygen -t ed25519 -C "huiyiyouheck@gmial.com"

# 或生成RSA密钥（兼容性更好）
ssh-keygen -t rsa -b 4096 -C "your-email@example.com"
```

**执行后会提示**：
```
Generating public/private ed25519 key pair.
Enter file in which to save the key (/root/.ssh/id_ed25519): 
```
- **直接回车**（使用默认路径）

```
Enter passphrase (empty for no passphrase): 
```
- **直接回车**（不设置密码，方便自动部署）
- 或输入密码（更安全，但每次使用需要输入）

```
Enter same passphrase again: 
```
- **再次回车**（或再次输入密码）

**成功后会显示**：
```
Your identification has been saved in /root/.ssh/id_ed25519
Your public key has been saved in /root/.ssh/id_ed25519.pub
```

### 第三步：查看公钥内容

```bash
# 查看公钥（复制全部内容）
cat ~/.ssh/id_ed25519.pub

# 如果没有id_ed25519，查看id_rsa
cat ~/.ssh/id_rsa.pub
```

**输出示例**：
```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx your-email@example.com
```

**⚠️ 重要**：复制**整个输出**，包括 `ssh-ed25519` 开头和邮箱结尾的整行。

### 第四步：将公钥添加到GitHub

#### 方法1：通过网页添加（推荐）

1. **访问GitHub设置页面**：
   ```
   https://github.com/settings/keys
   ```
   或：
   - 登录GitHub
   - 点击右上角头像 → **Settings**
   - 左侧菜单 → **SSH and GPG keys**

2. **添加新密钥**：
   - 点击 **"New SSH key"** 按钮
   - **Title**：填写描述（如：`阿里云ECS服务器`）
   - **Key type**：选择 `Authentication Key`
   - **Key**：粘贴刚才复制的公钥内容（整个输出）
   - 点击 **"Add SSH key"**

3. **确认添加**：
   - 可能需要输入GitHub密码确认

#### 方法2：使用GitHub CLI（如果已安装）

```bash
# 安装GitHub CLI（如果没有）
# Ubuntu/Debian:
apt install gh

# 登录GitHub
gh auth login

# 添加SSH密钥
gh ssh-key add ~/.ssh/id_ed25519.pub --title "阿里云ECS"
```

### 第五步：测试SSH连接

```bash
# 测试连接
ssh -T git@github.com
```

**成功输出**：
```
Hi huiyiyouck! You've successfully authenticated, but GitHub does not provide shell access.
```

**如果还是失败**，继续下面的故障排查。

### 第六步：重新克隆仓库

```bash
# 现在应该可以成功克隆了
git clone git@github.com:huiyiyouck/PhoneClone.git
```

## 🔧 故障排查

### 问题1：仍然提示 Permission denied

**检查1：确认公钥已正确添加**

```bash
# 查看本地公钥
cat ~/.ssh/id_ed25519.pub

# 访问GitHub确认已添加
# https://github.com/settings/keys
```

**检查2：测试SSH连接并查看详细信息**

```bash
# 使用详细模式测试
ssh -vT git@github.com

# 查看输出，确认：
# - 是否使用了正确的密钥文件
# - 是否有其他错误信息
```

**检查3：检查密钥权限**

```bash
# 设置正确的文件权限（重要！）
chmod 700 ~/.ssh
chmod 600 ~/.ssh/id_ed25519
chmod 644 ~/.ssh/id_ed25519.pub

# 验证权限
ls -la ~/.ssh/
```

**检查4：启动SSH agent并添加密钥**

```bash
# 启动SSH agent
eval "$(ssh-agent -s)"

# 添加密钥
ssh-add ~/.ssh/id_ed25519

# 如果设置了密码，会提示输入密码

# 验证密钥已添加
ssh-add -l

# 再次测试
ssh -T git@github.com
```

### 问题2：多个SSH密钥

如果你有多个SSH密钥，需要配置SSH config：

```bash
# 创建或编辑SSH config
nano ~/.ssh/config
```

添加以下内容：

```
Host github.com
    HostName github.com
    User git
    IdentityFile ~/.ssh/id_ed25519
    IdentitiesOnly yes
```

保存后测试：

```bash
ssh -T git@github.com
```

### 问题3：使用HTTPS替代（如果SSH配置太麻烦）

如果SSH配置遇到困难，可以使用HTTPS方式：

```bash
# 使用HTTPS克隆
git clone https://github.com/huiyiyouck/PhoneClone.git

# 需要输入：
# Username: huiyiyouck
# Password: 你的Personal Access Token（不是GitHub密码）
```

**生成Personal Access Token**：
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token (classic)
3. 选择权限：`repo`（完整仓库访问）
4. 生成并复制token
5. 使用token作为密码

## 📋 快速命令清单

```bash
# 1. 检查是否有密钥
ls -la ~/.ssh/

# 2. 如果没有，生成密钥
ssh-keygen -t ed25519 -C "your-email@example.com"
# 全部直接回车

# 3. 查看公钥
cat ~/.ssh/id_ed25519.pub
# 复制输出的全部内容

# 4. 添加到GitHub（通过网页）
# 访问：https://github.com/settings/keys
# 点击 "New SSH key"，粘贴公钥

# 5. 测试连接
ssh -T git@github.com

# 6. 设置权限（如果测试失败）
chmod 700 ~/.ssh
chmod 600 ~/.ssh/id_ed25519
chmod 644 ~/.ssh/id_ed25519.pub

# 7. 启动SSH agent（如果需要）
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519

# 8. 重新测试
ssh -T git@github.com

# 9. 克隆仓库
git clone git@github.com:huiyiyouck/PhoneClone.git
```

## ✅ 验证成功

克隆成功后，你应该看到：

```bash
Cloning into 'PhoneClone'...
remote: Enumerating objects: 132, done.
remote: Counting objects: 100% (132/132), done.
remote: Compressing objects: 100% (95/95), done.
remote: Total 132 (delta 37), reused 132 (delta 37), pack-reused 0
Receiving objects: 100% (132/132), done.
Resolving deltas: 100% (37/37), done.
```

然后可以进入目录：

```bash
cd PhoneClone
ls -la
```

---

**总结**：按照上述步骤操作，重点是**生成SSH密钥**和**将公钥添加到GitHub**。

