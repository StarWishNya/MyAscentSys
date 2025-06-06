# MyAscentSys Frontend

现代化的化学产品管理系统前端界面，用于与 MyAscentSys Java 后端进行交互。

## 🚀 快速开始

### Windows 系统

1. **安装 Node.js**
   - 下载并安装 [Node.js](https://nodejs.org/) (版本 14 或更高)

2. **启动代理服务器**
   ```cmd
   # 进入前端目录
   cd frontend
   
   # 运行启动脚本
   start-proxy.bat
   ```

3. **访问应用**
   - 打开浏览器访问: http://localhost:3000

### Linux 系统

#### 方法一：使用启动脚本
```bash
# 添加执行权限
chmod +x start-proxy.sh

# 启动代理服务器
./start-proxy.sh
```

#### 方法二：一键部署（推荐）
```bash
# 使用 root 权限运行部署脚本
sudo chmod +x deploy-linux.sh
sudo ./deploy-linux.sh
```

#### 方法三：Docker 部署
```bash
# 构建并运行
docker-compose up -d

# 或手动构建
docker build -t myascentsys-frontend .
docker run -d -p 3000:3000 --name myascentsys-proxy myascentsys-frontend
```

详细的 Linux 安装指南请参考 [LINUX-SETUP.md](LINUX-SETUP.md)

## 📋 系统要求

- **Node.js**: 14.0.0 或更高版本
- **操作系统**: Windows 10+, Ubuntu 18.04+, CentOS 7+, 或其他主流 Linux 发行版
- **内存**: 至少 512MB 可用内存
- **网络**: 需要访问 localhost:8083 (Java 后端)

## 🏗️ 项目架构

```
MyAscentSys Frontend
├── 前端界面 (HTML/CSS/JavaScript)
├── HTTP 代理服务器 (Node.js)
└── TCP 通信层
    └── Java 后端 (端口 8083)
        └── MySQL 数据库
```

### 通信流程
1. **浏览器** → HTTP 请求 → **Node.js 代理服务器** (端口 3000)
2. **代理服务器** → TCP Socket → **Java 后端** (端口 8083)
3. **Java 后端** → 数据库查询 → **MySQL**
4. 响应原路返回

## 🔧 核心功能

### 用户管理
- 🔐 用户登录/注册
- 👥 用户权限管理（管理员功能）
- 🔑 基于角色的访问控制

### 产品管理
- ➕ 添加化学产品
- 📝 编辑产品信息
- 🗑️ 删除产品
- 🔍 产品搜索和筛选
- 📊 产品库存管理

### 界面特性
- 📱 响应式设计，支持移动设备
- 🎨 现代化 UI 设计
- ⚡ 实时数据更新
- 🔔 操作反馈和通知

## 📁 文件结构

```
frontend/
├── index.html              # 主页面
├── styles.css              # 样式文件
├── app.js                  # 前端 JavaScript 逻辑
├── proxy-server.js         # Node.js 代理服务器
├── package.json            # Node.js 依赖配置
├── start-proxy.bat         # Windows 启动脚本
├── start-proxy.sh          # Linux 启动脚本
├── deploy-linux.sh         # Linux 一键部署脚本
├── Dockerfile              # Docker 配置
├── docker-compose.yml      # Docker Compose 配置
├── myascentsys-proxy.service # systemd 服务文件
├── README.md               # 项目说明
└── LINUX-SETUP.md          # Linux 详细安装指南
```

## 🔌 API 接口

### 用户相关
- `POST /api/login` - 用户登录
- `POST /api/register` - 用户注册
- `GET /api/users` - 获取用户列表（管理员）
- `PUT /api/users/:id` - 更新用户权限（管理员）
- `DELETE /api/users/:id` - 删除用户（管理员）

### 产品相关
- `GET /api/products` - 获取产品列表
- `POST /api/products` - 添加产品
- `PUT /api/products/:id` - 更新产品
- `DELETE /api/products/:id` - 删除产品
- `GET /api/products/search?q=keyword` - 搜索产品

### 系统相关
- `GET /health` - 健康检查

## 🛠️ 开发说明

### 本地开发
```bash
# 安装依赖
npm install

# 启动开发服务器
node proxy-server.js
```

### 环境变量
```bash
NODE_ENV=production        # 运行环境
PORT=3000                 # 代理服务器端口
BACKEND_HOST=localhost    # 后端主机
BACKEND_PORT=8083         # 后端端口
```

### 日志
- 代理服务器日志会输出到控制台
- 系统服务日志: `sudo journalctl -u myascentsys-proxy -f`

## 🔒 安全特性

- ✅ CORS 跨域配置
- ✅ 用户身份验证
- ✅ 基于角色的权限控制
- ✅ 输入数据验证
- ✅ SQL 注入防护（后端实现）

## 🐛 故障排除

### 常见问题

1. **代理服务器无法启动**
   - 检查端口 3000 是否被占用
   - 确认 Node.js 版本 >= 14.0.0

2. **无法连接后端**
   - 确认 Java 后端在端口 8083 运行
   - 检查防火墙设置

3. **权限错误 (Linux)**
   - 确保脚本有执行权限: `chmod +x start-proxy.sh`
   - 检查文件所有者: `chown user:group filename`

### 调试命令
```bash
# 检查端口使用
netstat -tulpn | grep :3000
lsof -i :3000

# 查看服务状态
systemctl status myascentsys-proxy

# 查看日志
journalctl -u myascentsys-proxy -f

# 手动启动调试
node proxy-server.js
```

## 📈 性能优化

- 使用 PM2 进行进程管理
- 配置 Nginx 反向代理
- 启用 Gzip 压缩
- 实施缓存策略

## 🔄 更新和维护

### 更新代码
```bash
# 停止服务
sudo systemctl stop myascentsys-proxy

# 更新文件
# ... 复制新文件 ...

# 重启服务
sudo systemctl start myascentsys-proxy
```

### 备份
- 定期备份配置文件
- 监控日志文件大小
- 定期更新 Node.js 依赖

## 🤝 贡献

1. Fork 本项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 支持

如果遇到问题，请：
1. 查看本文档的故障排除部分
2. 检查 [LINUX-SETUP.md](LINUX-SETUP.md) 详细指南
3. 提交 Issue 到项目仓库

---

**MyAscentSys Team** 🧪⚗️