package com.ruoyi.web.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 开启WebSocket支持
 * @author yufei
 */
@Configuration
public class WebSocketConfig {

//    TODO 如果用外置tomcat，要注释掉以下代码，否则启动项目会报错，用springboot内置tomcat就得放开以下代码
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}