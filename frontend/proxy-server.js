const http = require('http');
const net = require('net');
const url = require('url');
const fs = require('fs');
const path = require('path');

// 读取配置文件
let config = {};
try {
    const configFile = fs.readFileSync(path.join(__dirname, 'config.json'), 'utf8');
    config = JSON.parse(configFile);
} catch (error) {
    console.warn('配置文件读取失败，使用默认配置:', error.message);
    config = {
        development: { proxy: { port: 3000 }, backend: { host: 'localhost', port: 8083 } },
        production: { proxy: { port: 3000 }, backend: { host: 'localhost', port: 8083 } }
    };
}

// 获取当前环境 - 改为默认使用 production
const NODE_ENV = process.env.NODE_ENV || 'production';
const currentConfig = config[NODE_ENV] || config.production;

// 配置 - 优先使用环境变量，其次使用配置文件，最后使用默认值
const PROXY_PORT = process.env.PORT || currentConfig.proxy.port || 3000;
const BACKEND_HOST = process.env.BACKEND_HOST || currentConfig.backend.host || 'localhost';
const BACKEND_PORT = process.env.BACKEND_PORT || currentConfig.backend.port || 8083;

// 创建HTTP服务器
const server = http.createServer((req, res) => {
    // 设置CORS headers
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
    
    // 处理OPTIONS预检请求
    if (req.method === 'OPTIONS') {
        res.writeHead(200);
        res.end();
        return;
    }    // 解析请求
    const parsedUrl = url.parse(req.url, true);
    const path = parsedUrl.pathname;
    
    console.log(`[${new Date().toISOString()}] ${req.method} ${path}`);

    // 健康检查端点
    if (path === '/health') {
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({
            status: 'healthy',
            timestamp: new Date().toISOString(),
            service: 'MyAscentSys Proxy Server',
            version: '1.0.0'
        }));
        return;
    }

    // 静态文件服务
    if (path === '/' || path === '/index.html') {
        const fs = require('fs');
        const filePath = require('path').join(__dirname, 'index.html');
        fs.readFile(filePath, (err, data) => {
            if (err) {
                res.writeHead(404);
                res.end('File not found');
            } else {
                res.writeHead(200, {'Content-Type': 'text/html'});
                res.end(data);
            }
        });
        return;
    }

    if (path === '/styles.css') {
        const fs = require('fs');
        const filePath = require('path').join(__dirname, 'styles.css');
        fs.readFile(filePath, (err, data) => {
            if (err) {
                res.writeHead(404);
                res.end('File not found');
            } else {
                res.writeHead(200, {'Content-Type': 'text/css'});
                res.end(data);
            }
        });
        return;
    }

    if (path === '/app.js') {
        const fs = require('fs');
        const filePath = require('path').join(__dirname, 'app.js');
        fs.readFile(filePath, (err, data) => {
            if (err) {
                res.writeHead(404);
                res.end('File not found');
            } else {
                res.writeHead(200, {'Content-Type': 'application/javascript'});
                res.end(data);
            }
        });
        return;
    }

    // 收集请求数据
    let requestData = '';
    req.on('data', chunk => {
        requestData += chunk.toString();
    });

    req.on('end', () => {
        // 创建到后端TCP服务器的连接
        const client = new net.Socket();
          client.connect(BACKEND_PORT, BACKEND_HOST, () => {
            console.log(`Connected to backend server at ${BACKEND_HOST}:${BACKEND_PORT}`);
            
            // 根据路径转换请求格式为JSON
            let jsonMessage = {};
            
            try {
                if (path === '/api/login') {
                    const data = JSON.parse(requestData);
                    jsonMessage = {
                        function: 'login',
                        username: data.username,
                        password: data.password
                    };
                } else if (path === '/api/register') {
                    const data = JSON.parse(requestData);
                    jsonMessage = {
                        function: 'register',
                        username: data.username,
                        password: data.password,
                        authority: data.authority || 1
                    };
                } else if (path === '/api/products' && req.method === 'GET') {
                    jsonMessage = { function: 'getAllProducts' };
                } else if (path === '/api/products' && req.method === 'POST') {
                    const data = JSON.parse(requestData);
                    jsonMessage = {
                        function: 'insertProduct',
                        product: {
                            name: data.name,
                            price: data.price,
                            stock: data.stock,
                            cas: data.cas || '',
                            formula: data.formula || '',
                            category: data.category || '0',
                            structurePictureAddress: data.structureImage || ''
                        }
                    };
                } else if (path.startsWith('/api/products/') && req.method === 'PUT') {
                    const productId = path.split('/')[3];
                    const data = JSON.parse(requestData);
                    jsonMessage = {
                        function: 'updateProduct',
                        product: {
                            id: parseInt(productId),
                            name: data.name,
                            price: data.price,
                            stock: data.stock,
                            cas: data.cas || '',
                            formula: data.formula || '',
                            category: data.category || '0',
                            structurePictureAddress: data.structureImage || ''
                        }
                    };
                } else if (path.startsWith('/api/products/') && req.method === 'DELETE') {
                    const productId = path.split('/')[3];
                    jsonMessage = {
                        function: 'deleteProduct',
                        id: parseInt(productId)
                    };
                } else if (path === '/api/users' && req.method === 'GET') {
                    jsonMessage = { function: 'getAllUsers' };
                } else if (path.startsWith('/api/users/') && req.method === 'DELETE') {
                    const username = path.split('/')[3];
                    jsonMessage = {
                        function: 'deleteUser',
                        username: username
                    };
                } else if (path === '/api/password' && req.method === 'PUT') {
                    const data = JSON.parse(requestData);
                    jsonMessage = {
                        function: 'changePassword',
                        username: data.username,
                        oldPassword: data.oldPassword,
                        newPassword: data.newPassword
                    };
                } else if (path.startsWith('/api/search/')) {
                    const keyword = decodeURIComponent(path.split('/')[3]);
                    jsonMessage = {
                        function: 'queryProductByName',
                        name: keyword
                    };
                } else {
                    sendErrorResponse(res, 404, 'API endpoint not found');
                    return;
                }

                const messageString = JSON.stringify(jsonMessage);
                console.log(`Sending to backend: ${messageString}`);
                client.write(messageString + '\n');
                
            } catch (error) {
                console.error('Error parsing request:', error);
                sendErrorResponse(res, 400, 'Invalid request format');
                client.destroy();
                return;
            }
        });

        // 处理后端响应
        client.on('data', (data) => {
            const response = data.toString().trim();
            console.log(`Received from backend: ${response}`);
            
            try {
                // 解析后端响应并转换为JSON格式
                const jsonResponse = parseBackendResponse(response, path);
                
                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify(jsonResponse));
                
            } catch (error) {
                console.error('Error parsing backend response:', error);
                sendErrorResponse(res, 500, 'Backend response parsing error');
            }
            
            client.destroy();
        });

        client.on('error', (error) => {
            console.error('TCP connection error:', error);
            sendErrorResponse(res, 500, 'Backend connection failed');
        });

        client.on('close', () => {
            console.log('Connection to backend closed');
        });
    });
});

// 解析后端响应并转换为JSON格式
function parseBackendResponse(response, path) {
    try {
        // 后端返回的已经是JSON格式，直接解析
        const backendData = JSON.parse(response);
        
        // 将后端的响应格式转换为前端期望的格式
        if (path === '/api/login') {
            if (backendData.status === "1") {
                return {
                    success: true,
                    user: {
                        username: backendData.username || '',
                        authority: parseInt(backendData.authority || 0)
                    }
                };
            } else {
                return { 
                    success: false, 
                    message: backendData.message || 'Login failed' 
                };
            }
        } else if (path === '/api/register' || path === '/api/password' || 
                   path.startsWith('/api/users/') || path.startsWith('/api/products/')) {
            return {
                success: backendData.status === "1",
                message: backendData.message || (backendData.status === "1" ? 'Operation successful' : 'Operation failed')
            };
        } else if (path === '/api/products' || path.startsWith('/api/search/')) {
            if (backendData.status === "1") {
                // 处理产品列表
                let products = [];
                if (backendData.products && Array.isArray(backendData.products)) {
                    products = backendData.products;
                } else if (backendData.product) {
                    // 单个产品的情况
                    products = [backendData.product];
                }
                return { success: true, products };
            } else {
                return { 
                    success: false, 
                    message: backendData.message || 'Failed to get products' 
                };
            }
        } else if (path === '/api/users') {
            if (backendData.status === "1") {
                const users = backendData.users || [];
                return { success: true, users };
            } else {
                return { 
                    success: false, 
                    message: backendData.message || 'Failed to get users' 
                };
            }
        } else {
            return {
                success: backendData.status === "1",
                message: backendData.message || (backendData.status === "1" ? 'Operation successful' : 'Operation failed')
            };
        }
    } catch (error) {
        console.error('Error parsing backend JSON response:', error);
        return {
            success: false,
            message: 'Invalid response format from backend'
        };
    }
}

// 发送错误响应
function sendErrorResponse(res, statusCode, message) {
    res.writeHead(statusCode, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: false, message }));
}

// 启动服务器
server.listen(PROXY_PORT, () => {
    console.log('='.repeat(50));
    console.log('MyAscentSys Proxy Server Started');
    console.log('='.repeat(50));
    console.log(`Environment: ${NODE_ENV}`);
    console.log(`Proxy server running on http://localhost:${PROXY_PORT}`);
    console.log(`Backend server: ${BACKEND_HOST}:${BACKEND_PORT}`);
    console.log('Configuration source:');
    console.log(`  - PORT: ${process.env.PORT ? 'Environment Variable' : 'Config File/Default'}`);
    console.log(`  - BACKEND_HOST: ${process.env.BACKEND_HOST ? 'Environment Variable' : 'Config File/Default'}`);
    console.log(`  - BACKEND_PORT: ${process.env.BACKEND_PORT ? 'Environment Variable' : 'Config File/Default'}`);
    console.log('='.repeat(50));
    console.log('Ready to handle frontend requests...');
});

// 优雅关闭
process.on('SIGINT', () => {
    console.log('\nShutting down proxy server...');
    server.close(() => {
        console.log('Proxy server closed.');
        process.exit(0);
    });
});
