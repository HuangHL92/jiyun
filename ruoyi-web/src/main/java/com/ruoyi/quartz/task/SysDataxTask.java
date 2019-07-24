package com.ruoyi.quartz.task;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.common.DataXJsonCommon;
import com.ruoyi.system.domain.SysDataX;
import com.ruoyi.system.mapper.SysDataXMapper;
import com.ruoyi.system.service.ISysDataXService;
import com.ruoyi.web.websocket.SocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: Zen
 * @Date: 2019/04/08 9:26
 */
@Component("sysDataxTask")
public class SysDataxTask {
    @Autowired
    public ISysDataXService iSysDataXService;

    public void sysDataxParams(String fileNames) {
        // 执行同步
        System.out.println("执行有参方法：" + fileNames);
        //切割字符串
        String[] split = fileNames.split(",");
        List<String> fileNameList = Stream.of(split).collect(Collectors.toList());
        for(String fileName:fileNameList){
            SysDataX sysDataX = iSysDataXService.getOne(new QueryWrapper<SysDataX>().eq("file_name", fileName));
            if(sysDataX!=null){
                //执行同步命令
                
                String log = DataXJsonCommon.exeDataX(fileName);
                sysDataX.setLog(log);
                sysDataX.setUpdateBy("admin");
                iSysDataXService.updateById(sysDataX);
            }
        }
        SocketServer.sendMessage("执行有参方法！" + DateTime.now(),"onlineNotice");
    }
}
