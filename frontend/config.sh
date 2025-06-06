#!/bin/bash

echo "MyAscentSys 代理服务器配置管理工具"
echo

if [ $# -eq 0 ]; then
    echo "用法: ./config.sh [show|set|list] [参数...]"
    echo
    echo "命令:"
    echo "  show                    显示当前配置"
    echo "  set <env> <host> <port> 设置后端服务器地址"
    echo "  list                    列出所有环境配置"
    echo
    echo "示例:"
    echo "  ./config.sh show"
    echo "  ./config.sh set production 218.95.39.226 8083"
    echo "  ./config.sh set development localhost 8083"
    echo "  ./config.sh list"
    exit 0
fi

node config-manager.js "$@"
