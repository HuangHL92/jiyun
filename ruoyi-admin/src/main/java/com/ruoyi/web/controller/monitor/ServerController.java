package com.ruoyi.web.controller.monitor;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.framework.web.domain.Server;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务器监控
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/server")
public class ServerController extends BaseController
{
    private String prefix = "monitor/server";

    @Value("${management.server.port}")
    private String port;
    @RequiresPermissions("monitor:server:view")
    @GetMapping()
    public String server(ModelMap mmap, HttpServletRequest request) throws Exception
    {
        try{
            //调用/actuator/health 拿到json数据
            String jsonObj = HttpUtil.get(request.getScheme()+"://"+request.getServerName()+":"+port+"/actuator/health");
            JSONObject health = JSONUtil.parseObj(jsonObj);
            mmap.put("health",health);
        }catch (Exception e){
            //todo 此处特殊处理,不须抛出异常
        }
        Server server = new Server();
        server.copyTo();
        mmap.put("server", server);
        return prefix + "/server";
    }
}
