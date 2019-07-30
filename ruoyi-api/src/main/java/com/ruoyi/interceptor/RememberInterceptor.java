package com.ruoyi.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.AuthConstants;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.codec.Base64;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 定义一些页面需要做登录检查
 *
 * @author tao.liang
 * @date 2019/7/30
 */
public class RememberInterceptor extends HandlerInterceptorAdapter {

    private static ISysUserService userService = SpringUtils.getBean(ISysUserService.class);

    /**
     * 检查是否记住我，自动登录
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String redirectUrl = request.getParameter("redirectUri");

        // 判断是否记住我，自动登录
        Cookie rememberMeCookie = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberMe".equals(cookie.getName())) {
                    rememberMeCookie = cookie;
                    break;
                }
            }
        }

        if (rememberMeCookie == null || StrUtil.isBlank(rememberMeCookie.getValue())) {
            return true;
        } else {
            AES aes = SecureUtil.aes(Base64.decode(AuthConstants.AEC_KEY));
            String decryptStr = aes.decryptStr(rememberMeCookie.getValue());
            JSONObject jsonObject = JSONUtil.parseObj(decryptStr);
            String username= jsonObject.getStr("username");
            String password= jsonObject.getStr("password");
            //1. 登录验证
            Map<String, Object> checkMap = userService.checkLogin(username, password);
            Boolean loginResult = (Boolean) checkMap.get("result");
            SysUser correctUser = (SysUser) checkMap.get("user");
            //登录验证通过
            if (loginResult != null && loginResult) {

                //2. session中添加用户信息
                HttpSession session = request.getSession();
                session.setAttribute(AuthConstants.SESSION_USER, correctUser);
                if (StrUtil.isNotBlank(redirectUrl)) {
                    // 如果存在回调页面，则跳转到指定页面
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/user/userIndex");
                }
                return false;
            } else {
                // 删除无效cookie
                Cookie cookie = new Cookie(rememberMeCookie.getName(), null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                return true;
            }
        }
    }
}
