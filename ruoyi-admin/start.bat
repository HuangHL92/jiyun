@echo off
echo.
echo ========== [统一身份认证平台] 启动项目开始 ==========
echo.

cd %~dp0
cd ../ruoyi-admin/target

::set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m
::java -jar %JAVA_OPTS% tysfrzpt-admin.jar

set BUILD_ID=dontKillMe
start javaw -jar -Dspring.profiles.active=prod tysfrzpt-admin.jar
echo ========== [统一身份认证平台] 启动项目结束 ==========