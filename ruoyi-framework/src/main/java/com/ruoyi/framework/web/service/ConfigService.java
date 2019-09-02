package com.ruoyi.framework.web.service;

import com.ruoyi.common.config.Global;
import com.ruoyi.framework.util.CacheUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysDictData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.service.ISysConfigService;

import java.util.List;

/**
 * RuoYi首创 html调用 thymeleaf 实现参数管理
 * 
 * @author ruoyi
 */
@Service("config")
public class ConfigService
{
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private CacheUtils cacheUtils;

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数名称
     * @return 参数键值
     */
    public String getKey(String configKey)
    {
        //缓存处理
        String config = cacheUtils.getConfigCache().get(configKey);
        if(config==null) {
            config = configService.selectConfigByKey(configKey);
            cacheUtils.getConfigCache().put(configKey,config);
        }
        return config;
    }

    /**
     * 取得系统名称
     * @return
     */
    public String SystemName()
    {
        return Global.getName();
    }

    /**
     * 获取websocketServer地址
     * @return
     */
    public String WebSocketAddress()
    {
        return Global.getWebSocketAddress();
    }

    /**
     * 获取用户密码校验正则
     * @return
     */
    public String passwordRegex() {
        return Global.getPasswordRegex();
    }

    /**
     * 获取用户密码校验错误消息
     * @return
     */
    public String passwordMessage() {
        return Global.getPasswordMessage();
    }
}
