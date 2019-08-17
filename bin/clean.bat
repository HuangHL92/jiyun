@echo off
echo.
echo [JiYun] ·
echo.

%~d0
cd %~dp0

cd ..
call mvn clean

pause