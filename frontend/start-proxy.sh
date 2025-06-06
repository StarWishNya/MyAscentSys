#!/bin/bash

# MyAscentSys Frontend Proxy Se# 检查Java后端是否运行
echo -e "${YELLOW}检查Java后端服务器...${NC}"

# 检查netcat是否可用
if ! command -v nc &> /dev/null && ! command -v netcat &> /dev/null; then
    echo -e "${YELLOW}⚠ netcat未安装，跳过端口检查${NC}"
    echo "建议安装netcat: sudo apt-get install netcat 或 sudo yum install nc"
    SKIP_BACKEND_CHECK=true
else
    SKIP_BACKEND_CHECK=false
fi

if [ "$SKIP_BACKEND_CHECK" = false ]; then
    if ! nc -z localhost 8083 2>/dev/null && ! netcat -z localhost 8083 2>/dev/null; then
        echo -e "${RED}✗ Java后端服务器未在端口8083运行${NC}"
        echo "请先启动Java后端服务器:"
        echo "  cd ../MyAscentSys"
        echo "  java -cp target/classes com.ascent.Main"
        echo ""
        read -p "是否继续启动代理服务器? (y/N): " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    else
        echo -e "${GREEN}✓ Java后端服务器正在运行${NC}"
    fi
else
    echo -e "${YELLOW}⚠ 无法检查后端状态，请确保Java后端已启动${NC}"
fit for Linux
# Author: MyAscentSys Team
# Description: Starts the Node.js proxy server for connecting frontend to Java backend

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo -e "${BLUE}=== MyAscentSys Frontend Proxy Server ===${NC}"
echo -e "${BLUE}启动代理服务器...${NC}"
echo ""

# 检查前置条件
echo -e "${YELLOW}检查前置条件:${NC}"

# 检查Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}✗ Node.js 未安装${NC}"
    echo "请安装 Node.js (版本14或更高): https://nodejs.org/"
    exit 1
fi

NODE_VERSION=$(node --version)
echo -e "${GREEN}✓ Node.js 已安装: $NODE_VERSION${NC}"

# 检查npm
if ! command -v npm &> /dev/null; then
    echo -e "${RED}✗ npm 未安装${NC}"
    echo "请安装 npm 包管理器"
    exit 1
fi

NPM_VERSION=$(npm --version)
echo -e "${GREEN}✓ npm 已安装: $NPM_VERSION${NC}"

# 检查Java后端是否运行
echo -e "${YELLOW}检查Java后端服务器...${NC}"
if ! nc -z localhost 8083 2>/dev/null; then
    echo -e "${RED}✗ Java后端服务器未在端口8083运行${NC}"
    echo "请先启动Java后端服务器:"
    echo "  cd ../MyAscentSys"
    echo "  java -cp target/classes com.ascent.Main"
    echo ""
    read -p "是否继续启动代理服务器? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    echo -e "${GREEN}✓ Java后端服务器正在运行${NC}"
fi

# 检查端口3000是否被占用
echo -e "${YELLOW}检查端口3000...${NC}"

if [ "$SKIP_BACKEND_CHECK" = false ]; then
    if nc -z localhost 3000 2>/dev/null || netcat -z localhost 3000 2>/dev/null; then
        echo -e "${RED}✗ 端口3000已被占用${NC}"
        echo "请停止占用端口3000的进程或更改代理服务器端口"
        
        # 尝试找到占用端口的进程
        if command -v lsof &> /dev/null; then
            PID=$(lsof -ti:3000 2>/dev/null)
            if [ ! -z "$PID" ]; then
                echo "占用端口的进程ID: $PID"
                read -p "是否终止该进程? (y/N): " -n 1 -r
                echo ""
                if [[ $REPLY =~ ^[Yy]$ ]]; then
                    kill -9 $PID
                    echo -e "${GREEN}✓ 进程已终止${NC}"
                    sleep 1
                else
                    exit 1
                fi
            fi
        elif command -v ss &> /dev/null; then
            echo "请手动停止占用端口3000的进程"
            ss -tulpn | grep :3000
            exit 1
        else
            echo "请手动停止占用端口3000的进程"
            exit 1
        fi
    else
        echo -e "${GREEN}✓ 端口3000可用${NC}"
    fi
else
    echo -e "${YELLOW}⚠ 无法检查端口状态${NC}"
fi

echo ""

# 检查并安装依赖
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}首次运行，正在安装依赖...${NC}"
    npm install
    if [ $? -ne 0 ]; then
        echo -e "${RED}✗ 依赖安装失败${NC}"
        echo "请检查网络连接和npm配置"
        exit 1
    fi
    echo -e "${GREEN}✓ 依赖安装完成${NC}"
fi

# 检查关键文件
REQUIRED_FILES=("proxy-server.js" "package.json")
for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        echo -e "${RED}✗ 缺少必要文件: $file${NC}"
        exit 1
    fi
done

echo -e "${GREEN}✓ 所有文件检查通过${NC}"
echo ""

# 启动代理服务器
echo -e "${BLUE}启动代理服务器...${NC}"
echo -e "${YELLOW}代理服务器将在端口3000运行${NC}"
echo -e "${YELLOW}后端连接: localhost:8083${NC}"
echo -e "${YELLOW}前端访问: http://localhost:3000${NC}"
echo ""
echo -e "${BLUE}按 Ctrl+C 停止服务器${NC}"
echo -e "${GREEN}服务器启动成功！${NC}"
echo ""

# 设置信号处理
trap 'echo -e "\n${YELLOW}正在停止代理服务器...${NC}"; echo -e "${GREEN}代理服务器已停止${NC}"; exit 0' INT TERM

# 启动服务器
node proxy-server.js
