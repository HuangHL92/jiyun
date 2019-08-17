@echo off
echo.
echo [JiYun] ·
echo.

setlocal enabledelayedexpansion

set /p port=8081
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