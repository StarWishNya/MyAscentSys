@echo off
chcp 65001 >nul
echo MyAscentSys 代理服务器配置管理工具
echo.

if "%1"=="" (
    echo 用法: config.bat [show^|set^|list] [参数...]
    echo.
    echo 命令:
    echo   show                    显示当前配置
    echo   set ^<env^> ^<host^> ^<port^> 设置后端服务器地址
    echo   list                    列出所有环境配置
    echo.
    echo 示例:
    echo   config.bat show
    echo   config.bat set production 218.95.39.226 8083
    echo   config.bat set development localhost 8083
    echo   config.bat list
    goto :eof
)

node config-manager.js %*
