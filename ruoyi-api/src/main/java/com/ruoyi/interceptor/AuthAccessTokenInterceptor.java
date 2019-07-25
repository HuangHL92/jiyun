package com.ruoyi.interceptor;

import cn.hutool.json.JSONUtil;
import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于校验Access Token是否为空以及Access Token是否已经失效
 *
 * @author tao.liang
 * @date 2019/7/24
 */
public class AuthAccessTokenInterceptor extends HandlerInterceptorAdapter {

    private static final IAuthAccessTokenService authAccessTokenService = SpringUtils.getBean(IAuthAccessTokenService.class);
    /**
     * 检查Access Token是否已经失效
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getParameter("access_token");

        if (StringUtils.isNoneBlank(accessToken)) {
            //查询数据库中的Access Token
            AuthAccessToken authAccessToken = authAccessTokenService.selectByAccessToken(accessToken);

            if (authAccessToken != null) {
                Long savedExpiresAt = authAccessToken.getExpiresIn();
                //过期日期
                LocalDateTime expiresDateTime = DateUtils.ofEpochSecond(savedExpiresAt, null);
                //当前日期
                LocalDateTime nowDateTime = DateUtils.now();

                //如果Access Token已经失效，则返回错误提示
                return expiresDateTime.isAfter(nowDateTime) || this.generateErrorResponse(response, ResponseCode.EXPIRED_TOKEN);
            } else {
                return this.generateErrorResponse(response, ResponseCode.INVALID_GRANT);
            }
        } else {
            return this.generateErrorResponse(response, ResponseCode.INVALID_REQUEST);
        }
    }

    /**
     * 组装错误请求的返回
     */
    private boolean generateErrorResponse(HttpServletResponse response, ResponseCode responseCode) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", responseCode.getCode());
        result.put("msg", responseCode.getMsg());

        response.getWriter().write(JSONUtil.toJsonStr(result));
        return false;
    }

}
