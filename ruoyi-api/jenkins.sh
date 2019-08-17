#!/bin/bash
##### 停止先前的进程 #####
echo "Execute shell Start "
echo "Stopping tysfrzpt-api"
pid=`ps -ef | grep tysfrzpt-api.jar | grep -v grep | wc -l`
if [ "$pid" -gt "0" ];then
   pid=`ps -ef | grep tysfrzpt-api.jar | grep -v grep | awk '{print $2}'`
   echo "kill -9 的pid:"
   echo $pid
   kill -9 $pid
else
   echo "pid not exist"
fi
echo "Starting jar"
##### 创建新的进程 #####
BUILD_ID=dontKillMe nohup java -jar -Dspring.profiles.active=prod ruoyi-api/target/tysfrzpt-api.jar &
echo "Execute shell End"