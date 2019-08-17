@echo off
echo.
echo ========== [统一身份认证平台] 关闭之前项目 ==========
echo.

setlocal enabledelayedexpansion
set port=8081
for /f "tokens=1-5" %%a in ('netstat -ano ^| find ":%port%"') do (
 if "%%e%" == "" (
     set pid=%%d
 ) else (
     set pid=%%e
 )
 echo !pid!
 taskkill /f /pid !pid!
)
pause