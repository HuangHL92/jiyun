@echo off
echo.
echo ========== [统一身份认证平台] 关闭已启动项目 ==========
echo.

@echo off
set port=8081
for /f "tokens=1-5" %%i in ('netstat -ano^|findstr ":%port%"') do taskkill /pid %%m