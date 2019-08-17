@echo off
echo.
echo [JiYun] ·
echo.

cd %~dp0
cd ../ruoyi-admin/target

#set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m
#java -jar %JAVA_OPTS% tysfrzpt-admin.jar

java -jar tysfrzpt-admin.jar

cd bin
pause