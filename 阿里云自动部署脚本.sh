#!/bin/bash
# 阿里云ECS自动部署脚本
# 从GitHub拉取代码并部署

set -e

echo "=========================================="
echo "PhoneClone后端服务自动部署脚本"
echo "=========================================="

# 配置变量（根据实际情况修改）
GITHUB_REPO="https://github.com/your-username/phoneclone.git"
APP_DIR="/opt/phoneclone"
BRANCH="main"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查Java
check_java() {
    if ! command -v java &> /dev/null; then
        echo -e "${YELLOW}安装Java 21...${NC}"
        apt update
        apt install -y openjdk-21-jdk
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo -e "${GREEN}Java版本: $JAVA_VERSION${NC}"
}

# 检查Git
check_git() {
    if ! command -v git &> /dev/null; then
        echo -e "${YELLOW}安装Git...${NC}"
        apt update
        apt install -y git
    fi
}

# 克隆或更新代码
update_code() {
    echo -e "${YELLOW}更新代码...${NC}"
    
    if [ -d "$APP_DIR" ]; then
        echo "目录已存在，拉取最新代码..."
        cd $APP_DIR
        git fetch origin
        git reset --hard origin/$BRANCH
        git pull origin $BRANCH
    else
        echo "首次克隆代码..."
        mkdir -p $APP_DIR
        git clone -b $BRANCH $GITHUB_REPO $APP_DIR
        cd $APP_DIR
    fi
    
    echo -e "${GREEN}代码更新完成${NC}"
}

# 构建项目
build_project() {
    echo -e "${YELLOW}构建项目...${NC}"
    cd $APP_DIR/backend
    
    # 检查gradlew是否存在
    if [ ! -f "gradlew" ]; then
        echo -e "${YELLOW}gradlew不存在，尝试初始化...${NC}"
        # 如果系统有gradle，初始化wrapper
        if command -v gradle &> /dev/null; then
            gradle wrapper --gradle-version 8.5
        else
            echo -e "${RED}错误: gradlew不存在且系统未安装gradle${NC}"
            exit 1
        fi
    fi
    
    # 给gradlew添加执行权限
    chmod +x gradlew
    
    # 构建
    ./gradlew clean build -x test
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}构建成功${NC}"
    else
        echo -e "${RED}构建失败${NC}"
        exit 1
    fi
}

# 检查配置文件
check_config() {
    echo -e "${YELLOW}检查配置文件...${NC}"
    
    if [ ! -f "$APP_DIR/backend/application-production.yml" ]; then
        echo -e "${YELLOW}配置文件不存在，创建模板...${NC}"
        cat > $APP_DIR/backend/application-production.yml <<EOF
spring:
  datasource:
    url: jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
    username: postgres
    password: 请修改为你的Supabase密码
  security:
    jwt:
      secret: 请修改为至少32字符的JWT密钥
server:
  port: 8080
  servlet:
    context-path: /api
EOF
        echo -e "${RED}请编辑配置文件: $APP_DIR/backend/application-production.yml${NC}"
        echo "然后重新运行此脚本"
        exit 1
    fi
    
    echo -e "${GREEN}配置文件检查通过${NC}"
}

# 停止旧服务
stop_service() {
    echo -e "${YELLOW}停止旧服务...${NC}"
    if systemctl is-active --quiet phoneclone-backend; then
        systemctl stop phoneclone-backend
        echo -e "${GREEN}服务已停止${NC}"
    else
        echo "服务未运行"
    fi
}

# 备份旧版本
backup_old_version() {
    if [ -f "$APP_DIR/backend/build/libs/phoneclone-backend-1.0.0.jar" ]; then
        echo -e "${YELLOW}备份旧版本...${NC}"
        BACKUP_DIR="$APP_DIR/backups/$(date +%Y%m%d_%H%M%S)"
        mkdir -p $BACKUP_DIR
        cp $APP_DIR/backend/build/libs/phoneclone-backend-1.0.0.jar $BACKUP_DIR/
        echo -e "${GREEN}备份完成: $BACKUP_DIR${NC}"
    fi
}

# 启动服务
start_service() {
    echo -e "${YELLOW}启动服务...${NC}"
    
    # 检查systemd服务是否存在
    if [ ! -f "/etc/systemd/system/phoneclone-backend.service" ]; then
        echo -e "${YELLOW}创建systemd服务...${NC}"
        create_systemd_service
    fi
    
    systemctl daemon-reload
    systemctl enable phoneclone-backend
    systemctl start phoneclone-backend
    
    sleep 3
    
    if systemctl is-active --quiet phoneclone-backend; then
        echo -e "${GREEN}服务启动成功${NC}"
        systemctl status phoneclone-backend --no-pager -l
    else
        echo -e "${RED}服务启动失败，查看日志:${NC}"
        journalctl -u phoneclone-backend -n 50 --no-pager
        exit 1
    fi
}

# 创建systemd服务
create_systemd_service() {
    cat > /tmp/phoneclone-backend.service <<EOF
[Unit]
Description=PhoneClone Backend Service
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=$APP_DIR/backend
ExecStart=/usr/bin/java -Xmx512m -Xms256m -jar $APP_DIR/backend/build/libs/phoneclone-backend-1.0.0.jar --spring.config.location=classpath:/application.yml,file:$APP_DIR/backend/application-production.yml --spring.profiles.active=production
Environment="JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64"
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

    mv /tmp/phoneclone-backend.service /etc/systemd/system/
    echo -e "${GREEN}systemd服务文件已创建${NC}"
}

# 主函数
main() {
    echo "开始部署..."
    
    # 检查必要工具
    check_java
    check_git
    
    # 更新代码
    update_code
    
    # 构建项目
    build_project
    
    # 检查配置
    check_config
    
    # 停止旧服务
    stop_service
    
    # 备份
    backup_old_version
    
    # 启动服务
    start_service
    
    echo ""
    echo -e "${GREEN}=========================================="
    echo "部署完成！"
    echo "==========================================${NC}"
    echo ""
    echo "查看服务状态: systemctl status phoneclone-backend"
    echo "查看日志: journalctl -u phoneclone-backend -f"
    echo "测试API: curl http://localhost:8080/api/auth/login"
}

# 执行主函数
main

