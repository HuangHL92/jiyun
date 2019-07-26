package com.ruoyi.web.controller.system;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.web.core.config.JiYunOAuht2Config;
import com.ruoyi.web.websocket.SocketServer;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * 第三方登录 - 吉运
 *
 * @author tao.liang
 * @date 2019年7月26日
 */
@Controller
@RequestMapping("/oauth2/jiyun")
public class JiYunOAuth2Controller {

    @Autowired
    private JiYunOAuht2Config config;

    /**
     * 登录验证（实际登录调用认证服务器）
     *
     * @return
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 如果已经登录直接返回首页
        if (ShiroUtils.getSysUser() != null) {
            response.sendRedirect("/index");
        }

        //当前系统请求认证服务器成功之后返回的Authorization Code
        String code = request.getParameter("code");

        //code为空，则说明当前请求不是认证服务器的回调请求，则重定向URL到认证服务器登录
        if(StrUtil.isBlank(code)){
            String authorizeUrl = MessageFormat.format(config.getAuthorizationUri(), config.getClientId(), config.getRedirectUri());
            // 生成授权页面
            response.sendRedirect(authorizeUrl);
            return null;
        } else {
            //2. 通过Authorization Code获取Access Token
            String accessTokenUri = MessageFormat.format(config.getAccessTokenUri(), config.getClientId(), config.getClientSecret(), config.getRedirectUri(), code);
            JSONObject accessTokenObject = JSONUtil.parseObj(HttpUtil.post(accessTokenUri, new HashMap<>()));

            String accessToken = accessTokenObject.getStr("access_token");
            //如果正常返回
            if(StringUtils.isNoneBlank(accessToken)){
                System.out.println(accessTokenObject.toJSONString(4));

                // 再次查询用户基础信息，并将用户ID存到session
                String userInfoUri = MessageFormat.format(config.getUserInfoUri(), accessToken);
                JSONObject userInfoObject = JSONUtil.parseObj(HttpUtil.post(userInfoUri, new HashMap<>()));
                SysUser user = JSONUtil.toBean(userInfoObject, SysUser.class);

                // 5. 执行登录操作
                UsernamePasswordToken token = new UsernamePasswordToken(user.getLoginName(), "", false, "http://51e.com.cn");
                Subject subject = SecurityUtils.getSubject();
                try {
                    subject.login(token);

                    //socket消息通知
                    String msg = user.getUserName() + "，上线啦！";
                    SocketServer.sendMessage(msg, "onlineNotice");

                    return "redirect:/index";
                } catch (AuthenticationException e) {
                    return "error";
                }
            } else {
                return "error";
            }
        }
    }

    /**
     * 取消授权
     *
     * @param token
     * @return
     */
    @RequestMapping("/revoke/{token}")
    @ResponseBody
    public Object revokeAuth(@PathVariable("token") String token) {
        AuthRequest authRequest = getGiteeAuthRequest();
        return authRequest.revoke(token);
    }

    /**
     * 创建授权request
     *
     * @return
     */
    private AuthRequest getGiteeAuthRequest() {
        return new AuthGiteeRequest(AuthConfig.builder()
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .redirectUri(config.getRedirectUri())
                .build());
    }

}