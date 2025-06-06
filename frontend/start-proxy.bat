@echo off
echo 启动MyAscentSys前端代理服务器...
echo.
echo 请确保：
echo 1. 已安装Node.js (版本14或更高)
echo 2. Java后端服务器正在运行 (端口8083)
echo.

cd /d "%~dp0"

if not exist "node_modules" (
    echo 首次运行，正在初始化...
    npm install
    if errorlevel 1 (
        echo.
        echo 初始化失败，请检查Node.js是否正确安装
        pause
        exit /b 1
    )
)

echo 启动代理服务器...
node proxy-server.js

pause
