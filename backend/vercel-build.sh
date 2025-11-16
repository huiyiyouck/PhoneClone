#!/bin/bash
# Vercel构建脚本

echo "开始构建Spring Boot应用..."

# 构建JAR文件
./gradlew clean build -x test

# 检查构建是否成功
if [ $? -eq 0 ]; then
    echo "构建成功！"
    exit 0
else
    echo "构建失败！"
    exit 1
fi

