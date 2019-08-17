@echo off & color 3d & setlocal enabledelayedexpansion
  ::ipconfig>ip.txt

echo.
echo ========== [统一身份认证平台] 关闭已启动项目 ==========
echo.
set /p port=8081
netstat -aon |findstr %port%>pid.txt

     for /f "delims=" %%a in (pid.txt) do (

      for /f "tokens=1* delims=:" %%i in ('call echo %%a^|find /i "TCP"') do (
         echo %%a
     ::读取出内容过滤后,写入另一个记事本中
     rem Echo %%a>>"text.txt"
        )
    )
rem 读取文件中内容
set /P OEM=<pid.txt

rem 截取文件中的字符串

echo  %OEM:~71,76%

taskkill /f /pid %OEM:~71,76%