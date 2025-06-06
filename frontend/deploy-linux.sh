#!/bin/bash

# MyAscentSys Frontend 一键部署脚本
# 适用于 Ubuntu/Debian 和 CentOS/RHEL 系统

set -e  # 遇到错误时退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
APP_NAME="MyAscentSys"
SERVICE_USER="myascentsys"
APP_DIR="/opt/MyAscentSys"
FRONTEND_DIR="$APP_DIR/frontend"
SERVICE_NAME="myascentsys-proxy"

echo -e "${BLUE}=== MyAscentSys Frontend 部署脚本 ===${NC}"
echo -e "${BLUE}开始自动部署...${NC}"
echo ""

# 检测操作系统
detect_os() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$NAME
        DISTRO=$ID
    else
        echo -e "${RED}无法检测操作系统${NC}"
        exit 1
    fi
    echo -e "${GREEN}检测到操作系统: $OS${NC}"
}

# 安装依赖
install_dependencies() {
    echo -e "${YELLOW}安装系统依赖...${NC}"
    
    if [[ "$DISTRO" == "ubuntu" ]] || [[ "$DISTRO" == "debian" ]]; then
        sudo apt update
        sudo apt install -y curl wget gnupg2 software-properties-common netcat lsof
        
        # 安装 Node.js
        curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash -
        sudo apt install -y nodejs
        
    elif [[ "$DISTRO" == "centos" ]] || [[ "$DISTRO" == "rhel" ]] || [[ "$DISTRO" == "fedora" ]]; then
        if command -v dnf &> /dev/null; then
            sudo dnf install -y curl wget nc lsof
            # 安装 Node.js
            curl -fsSL https://rpm.nodesource.com/setup_lts.x | sudo bash -
            sudo dnf install -y nodejs
        else
            sudo yum install -y curl wget nc lsof
            # 安装 Node.js
            curl -fsSL https://rpm.nodesource.com/setup_lts.x | sudo bash -
            sudo yum install -y nodejs
        fi
    else
        echo -e "${RED}不支持的操作系统: $DISTRO${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}系统依赖安装完成${NC}"
}

# 创建用户
create_user() {
    echo -e "${YELLOW}创建应用用户...${NC}"
    
    if ! id "$SERVICE_USER" &>/dev/null; then
        sudo useradd -r -s /bin/false -d "$APP_DIR" "$SERVICE_USER"
        echo -e "${GREEN}用户 $SERVICE_USER 创建成功${NC}"
    else
        echo -e "${GREEN}用户 $SERVICE_USER 已存在${NC}"
    fi
}

# 创建目录结构
create_directories() {
    echo -e "${YELLOW}创建目录结构...${NC}"
    
    sudo mkdir -p "$APP_DIR"
    sudo mkdir -p "$FRONTEND_DIR"
    sudo mkdir -p "/var/log/myascentsys"
    
    echo -e "${GREEN}目录结构创建完成${NC}"
}

# 复制文件
copy_files() {
    echo -e "${YELLOW}复制应用文件...${NC}"
    
    # 获取当前脚本所在目录
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    
    # 复制前端文件
    sudo cp "$SCRIPT_DIR"/*.js "$FRONTEND_DIR/"
    sudo cp "$SCRIPT_DIR"/*.html "$FRONTEND_DIR/"
    sudo cp "$SCRIPT_DIR"/*.css "$FRONTEND_DIR/"
    sudo cp "$SCRIPT_DIR"/package*.json "$FRONTEND_DIR/"
    
    # 设置权限
    sudo chown -R "$SERVICE_USER:$SERVICE_USER" "$APP_DIR"
    sudo chmod 755 "$FRONTEND_DIR"
    
    echo -e "${GREEN}文件复制完成${NC}"
}

# 安装 Node.js 依赖
install_node_dependencies() {
    echo -e "${YELLOW}安装 Node.js 依赖...${NC}"
    
    cd "$FRONTEND_DIR"
    sudo -u "$SERVICE_USER" npm install --production
    
    echo -e "${GREEN}Node.js 依赖安装完成${NC}"
}

# 创建系统服务
create_service() {
    echo -e "${YELLOW}创建系统服务...${NC}"
    
    sudo tee /etc/systemd/system/"$SERVICE_NAME".service > /dev/null <<EOF
[Unit]
Description=MyAscentSys Frontend Proxy Server
After=network.target
Wants=network.target

[Service]
Type=simple
User=$SERVICE_USER
Group=$SERVICE_USER
WorkingDirectory=$FRONTEND_DIR
ExecStart=/usr/bin/node proxy-server.js
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=$SERVICE_NAME

Environment=NODE_ENV=production
Environment=PORT=3000
Environment=BACKEND_HOST=localhost
Environment=BACKEND_PORT=8083

NoNewPrivileges=true
PrivateTmp=true

[Install]
WantedBy=multi-user.target
EOF

    sudo systemctl daemon-reload
    sudo systemctl enable "$SERVICE_NAME"
    
    echo -e "${GREEN}系统服务创建完成${NC}"
}

# 配置防火墙
configure_firewall() {
    echo -e "${YELLOW}配置防火墙...${NC}"
    
    if command -v ufw &> /dev/null; then
        sudo ufw allow 3000/tcp
        echo -e "${GREEN}UFW 防火墙配置完成${NC}"
    elif command -v firewall-cmd &> /dev/null; then
        sudo firewall-cmd --permanent --add-port=3000/tcp
        sudo firewall-cmd --reload
        echo -e "${GREEN}firewalld 防火墙配置完成${NC}"
    else
        echo -e "${YELLOW}未检测到防火墙，请手动配置端口 3000${NC}"
    fi
}

# 启动服务
start_service() {
    echo -e "${YELLOW}启动服务...${NC}"
    
    sudo systemctl start "$SERVICE_NAME"
    sleep 3
    
    if sudo systemctl is-active --quiet "$SERVICE_NAME"; then
        echo -e "${GREEN}服务启动成功${NC}"
    else
        echo -e "${RED}服务启动失败${NC}"
        sudo systemctl status "$SERVICE_NAME"
        exit 1
    fi
}

# 显示部署信息
show_info() {
    echo ""
    echo -e "${BLUE}=== 部署完成 ===${NC}"
    echo -e "${GREEN}MyAscentSys Frontend 已成功部署！${NC}"
    echo ""
    echo -e "${YELLOW}访问地址:${NC} http://localhost:3000"
    echo -e "${YELLOW}服务状态:${NC} sudo systemctl status $SERVICE_NAME"
    echo -e "${YELLOW}查看日志:${NC} sudo journalctl -u $SERVICE_NAME -f"
    echo -e "${YELLOW}重启服务:${NC} sudo systemctl restart $SERVICE_NAME"
    echo -e "${YELLOW}停止服务:${NC} sudo systemctl stop $SERVICE_NAME"
    echo ""
    echo -e "${BLUE}确保 Java 后端在端口 8083 运行！${NC}"
    echo ""
}

# 主要部署流程
main() {
    detect_os
    install_dependencies
    create_user
    create_directories
    copy_files
    install_node_dependencies
    create_service
    configure_firewall
    start_service
    show_info
}

# 检查是否以 root 权限运行
if [[ $EUID -ne 0 ]]; then
   echo -e "${RED}此脚本需要 root 权限运行${NC}"
   echo "请使用: sudo $0"
   exit 1
fi

# 运行主函数
main

echo -e "${GREEN}部署脚本执行完毕！${NC}"
