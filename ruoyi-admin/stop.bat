@echo off
echo.
echo ========== [统一身份认证平台] 关闭已启动项目 ==========
echo.

set port=8081
for /f "tokens=1-5" %%i in ('netstat -ano^|findstr ":%port%"') do (
    tasklist /FI "PID eq %%m"|find /i "PID" && (
            echo PID:%%m 运行中,kill the process [%%m] who use the port [%port%]
            taskkill /F /pid %%m
            ) || echo PID:%%m 未运行
)