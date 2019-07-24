package com.ruoyi.interceptor;

import com.ruoyi.common.AuthConstants;
import com.ruoyi.common.SpringContextUtils;
import com.ruoyi.system.domain.SysUser;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 定义一些页面需要做登录检查
 *
 * @author tao.liang
 * @date 2019/7/24
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    /**
     * 检查是否已经登录
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        //获取session中存储的token
        SysUser user = (SysUser) session.getAttribute(AuthConstants.SESSION_USER);

        if (user != null) {
            return true;
        } else {
            //如果token不存在，则跳转到登录页面
            response.sendRedirect(request.getContextPath() + "/login?redirectUri=" + SpringContextUtils.getRequestUrl(request));

            return false;
        }
    }
}
