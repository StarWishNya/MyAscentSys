# MyAscentSys Frontend - Linux 安装与部署指南

## 前置要求

### 1. 系统要求
- Linux 发行版 (Ubuntu 18.04+, CentOS 7+, RHEL 7+, 等)
- 至少 512MB 可用内存
- 网络连接

### 2. 安装 Node.js

#### Ubuntu/Debian:
```bash
# 使用 NodeSource 仓库安装最新 LTS 版本
curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash -
sudo apt-get install -y nodejs

# 或使用包管理器安装
sudo apt update
sudo apt install nodejs npm
```

#### CentOS/RHEL/Fedora:
```bash
# 使用 NodeSource 仓库
curl -fsSL https://rpm.nodesource.com/setup_lts.x | sudo bash -
sudo yum install nodejs npm

# 或使用 dnf (Fedora)
sudo dnf install nodejs npm
```

#### 验证安装:
```bash
node --version    # 应显示 v14.0.0 或更高版本
npm --version     # 应显示对应的 npm 版本
```

### 3. 安装辅助工具

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install netcat lsof curl wget
```

#### CentOS/RHEL:
```bash
sudo yum install nc lsof curl wget
```

## 快速启动

### 1. 设置权限并启动
```bash
# 进入前端目录
cd /path/to/MyAscentSys/frontend

# 添加执行权限
chmod +x start-proxy.sh

# 启动代理服务器
./start-proxy.sh
```

### 2. 首次运行
脚本会自动检查并安装 Node.js 依赖，然后启动代理服务器。

### 3. 访问应用
打开浏览器访问: http://localhost:3000

## 系统服务部署

### 1. 创建系统服务文件
```bash
sudo nano /etc/systemd/system/myascentsys-proxy.service
```

### 2. 服务文件内容
```ini
[Unit]
Description=MyAscentSys Frontend Proxy Server
After=network.target

[Service]
Type=simple
User=your-username
WorkingDirectory=/path/to/MyAscentSys/frontend
ExecStart=/usr/bin/node proxy-server.js
Restart=always
RestartSec=10
Environment=NODE_ENV=production

[Install]
WantedBy=multi-user.target
```

### 3. 启用和启动服务
```bash
# 重新加载 systemd
sudo systemctl daemon-reload

# 启用服务（开机自启）
sudo systemctl enable myascentsys-proxy.service

# 启动服务
sudo systemctl start myascentsys-proxy.service

# 检查服务状态
sudo systemctl status myascentsys-proxy.service
```

### 4. 服务管理命令
```bash
# 停止服务
sudo systemctl stop myascentsys-proxy.service

# 重启服务
sudo systemctl restart myascentsys-proxy.service

# 查看日志
sudo journalctl -u myascentsys-proxy.service -f
```

## Docker 部署

### 1. 创建 Dockerfile
```dockerfile
FROM node:16-alpine

WORKDIR /app

# 复制 package 文件
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production

# 复制应用文件
COPY . .

# 暴露端口
EXPOSE 3000

# 启动应用
CMD ["node", "proxy-server.js"]
```

### 2. 构建并运行 Docker 容器
```bash
# 构建镜像
docker build -t myascentsys-frontend .

# 运行容器
docker run -d \
  --name myascentsys-proxy \
  -p 3000:3000 \
  --restart unless-stopped \
  myascentsys-frontend
```

## 反向代理配置

### Nginx 配置
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

### Apache 配置
```apache
<VirtualHost *:80>
    ServerName your-domain.com
    
    ProxyPreserveHost On
    ProxyRequests Off
    ProxyPass / http://localhost:3000/
    ProxyPassReverse / http://localhost:3000/
    
    Header set Access-Control-Allow-Origin "*"
</VirtualHost>
```

## 防火墙配置

### UFW (Ubuntu):
```bash
# 允许端口 3000
sudo ufw allow 3000/tcp

# 如果使用 Nginx/Apache
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
```

### firewalld (CentOS/RHEL):
```bash
# 允许端口 3000
sudo firewall-cmd --permanent --add-port=3000/tcp
sudo firewall-cmd --reload

# 如果使用 Nginx/Apache
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

## 故障排除

### 1. 端口被占用
```bash
# 查看端口使用情况
sudo netstat -tulpn | grep :3000
# 或
sudo ss -tulpn | grep :3000

# 杀死占用端口的进程
sudo kill -9 <PID>
```

### 2. Node.js 版本问题
```bash
# 使用 nvm 管理 Node.js 版本
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install --lts
nvm use --lts
```

### 3. 权限问题
```bash
# 检查文件权限
ls -la start-proxy.sh

# 修复权限
chmod +x start-proxy.sh
sudo chown -R $USER:$USER .
```

### 4. 查看日志
```bash
# 运行脚本时查看详细输出
./start-proxy.sh

# 如果是系统服务
sudo journalctl -u myascentsys-proxy.service -f
```

## 性能优化

### 1. PM2 进程管理
```bash
# 安装 PM2
npm install -g pm2

# 启动应用
pm2 start proxy-server.js --name myascentsys-proxy

# 保存 PM2 配置
pm2 save

# 设置开机自启
pm2 startup
```

### 2. 内存和CPU监控
```bash
# 查看进程资源使用
top -p $(pgrep -f proxy-server.js)

# 使用 htop (需要安装)
htop
```

## 安全建议

1. **使用 HTTPS**: 配置 SSL 证书
2. **防火墙**: 只开放必要端口
3. **反向代理**: 使用 Nginx 或 Apache 作为前端
4. **用户权限**: 不要使用 root 用户运行应用
5. **日志监控**: 定期检查应用日志
6. **更新依赖**: 定期更新 Node.js 和 npm 包

## 支持

如果遇到问题，请检查:
1. Java 后端是否在端口 8083 运行
2. Node.js 版本是否符合要求 (>=14.0.0)
3. 防火墙设置是否正确
4. 系统日志是否有错误信息
