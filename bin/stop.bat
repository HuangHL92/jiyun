@echo off
echo.
echo ========== [统一身份认证平台] 关闭之前项目 ==========
echo.

setlocal enabledelayedexpansion
set port=9000
set pid=-1
for /f "tokens=1-5" %%a in ('netstat -ano ^| find ":%port%"') do (
 if !pid! neq %%e (
    if %%e neq 0 (
        if '%%d' == 'LISTENING' (
           set pid=%%e
           echo !pid!
           taskkill /f /pid !pid!
        )
    )
 )
)

set port=9001
set pid=-1
for /f "tokens=1-5" %%a in ('netstat -ano ^| find ":%port%"') do (
 if !pid! neq %%e (
    if %%e neq 0 (
        if '%%d' == 'LISTENING' (
           set pid=%%e
           echo !pid!
           taskkill /f /pid !pid!
        )
    )
 )
)
pause
exit