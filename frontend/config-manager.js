#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const configPath = path.join(__dirname, 'config.json');

// 显示帮助信息
function showHelp() {
    console.log(`
MyAscentSys 代理服务器配置管理工具

用法: node config-manager.js [command] [options]

命令:
  show                    显示当前配置
  set <env> <host> <port> 设置后端服务器地址
  list                    列出所有环境配置

示例:
  node config-manager.js show
  node config-manager.js set production 218.95.39.226 8083
  node config-manager.js set development localhost 8083
  node config-manager.js list
    `);
}

// 读取配置文件
function readConfig() {
    try {
        const configData = fs.readFileSync(configPath, 'utf8');
        return JSON.parse(configData);
    } catch (error) {
        console.error('配置文件读取失败:', error.message);
        return null;
    }
}

// 写入配置文件
function writeConfig(config) {
    try {
        fs.writeFileSync(configPath, JSON.stringify(config, null, 2), 'utf8');
        console.log('配置已保存');
        return true;
    } catch (error) {
        console.error('配置文件写入失败:', error.message);
        return false;
    }
}

// 显示当前配置
function showConfig() {
    const config = readConfig();
    if (!config) return;

    const env = process.env.NODE_ENV || 'development';
    console.log(`当前环境: ${env}`);
    console.log('\n所有配置:');
    console.log(JSON.stringify(config, null, 2));
    
    if (config[env]) {
        console.log(`\n当前环境配置:`);
        console.log(`  代理端口: ${config[env].proxy.port}`);
        console.log(`  后端主机: ${config[env].backend.host}`);
        console.log(`  后端端口: ${config[env].backend.port}`);
    }
}

// 设置后端服务器地址
function setBackendServer(env, host, port) {
    const config = readConfig();
    if (!config) return;

    if (!config[env]) {
        config[env] = {
            proxy: { port: 3000 },
            backend: { host: 'localhost', port: 8083 }
        };
    }

    config[env].backend.host = host;
    config[env].backend.port = parseInt(port);

    if (writeConfig(config)) {
        console.log(`已更新 ${env} 环境配置:`);
        console.log(`  后端主机: ${host}`);
        console.log(`  后端端口: ${port}`);
    }
}

// 列出所有环境配置
function listConfig() {
    const config = readConfig();
    if (!config) return;

    console.log('所有环境配置:');
    Object.keys(config).forEach(env => {
        console.log(`\n${env}:`);
        console.log(`  代理端口: ${config[env].proxy.port}`);
        console.log(`  后端主机: ${config[env].backend.host}`);
        console.log(`  后端端口: ${config[env].backend.port}`);
    });
}

// 主程序
const args = process.argv.slice(2);
const command = args[0];

switch (command) {
    case 'show':
        showConfig();
        break;
    case 'set':
        if (args.length < 4) {
            console.error('错误: 缺少参数');
            console.log('用法: node config-manager.js set <env> <host> <port>');
            process.exit(1);
        }
        setBackendServer(args[1], args[2], args[3]);
        break;
    case 'list':
        listConfig();
        break;
    case 'help':
    case '--help':
    case '-h':
        showHelp();
        break;
    default:
        if (!command) {
            showHelp();
        } else {
            console.error(`未知命令: ${command}`);
            showHelp();
            process.exit(1);
        }
}
