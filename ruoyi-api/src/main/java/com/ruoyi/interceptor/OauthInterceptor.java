package com.ruoyi.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.common.AuthConstants;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 检查是否已经存在授权
 *
 * @author tao.liang
 * @date 2019/7/24
 */
public class OauthInterceptor extends HandlerInterceptorAdapter {

    private static final IAuthClientDetailsService authClientDetailsService = SpringUtils.getBean(IAuthClientDetailsService.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        //参数信息
        // String params = "?redirectUri=" + SpringContextUtils.getRequestUrl(request);

        //客户端ID
        String clientIdStr = request.getParameter("client_id");
        //回调URL
        String redirectUri = request.getParameter("redirect_uri");
        //返回形式
        String responseType = request.getParameter("response_type");

        //获取session中存储的token
        SysUser user = (SysUser) session.getAttribute(AuthConstants.SESSION_USER);

        if (StringUtils.isNoneBlank(clientIdStr)
                && StringUtils.isNoneBlank(redirectUri)
                && "code".equals(responseType)) {

            //1. 查询是否存在授权信息
            AuthClientDetails clientDetails = authClientDetailsService.selectByClientId(clientIdStr);

            if (clientDetails == null) {
                return this.generateErrorResponse(response, ResponseCode.INVALID_CLIENT);
            }

            if (!clientDetails.getRedirectUri().equals(redirectUri)) {
                return this.generateErrorResponse(response, ResponseCode.REDIRECT_URI_MISMATCH);
            }
            //校验应用是否开启
            if (!AuthClientDetails.STATUS_NORMAL.equals(clientDetails.getScope())) {
                return generateErrorResponse(response, ResponseCode.CLIENT_FORBIDDEN);
            }
            //校验授权范围
            if (StrUtil.isEmpty(clientDetails.getScope()) || !clientDetails.getScope().contains(AuthClientDetails.AUTH_SCOPE_PASSWORD)) {
                return generateErrorResponse(response, ResponseCode.INVALID_AUTH_SCOPE);
            }

            return true;
            // TODO
            /*//2. 查询用户给接入的客户端是否已经授权
            AuthClientUser clientUser = authClientUserMapper.selectByClientId(clientDetails.getId(), user.getId();
            if (clientUser != null) {
                return true;
            } else {
                //如果没有授权，则跳转到授权页面
                response.sendRedirect(request.getContextPath() + "/oauth2.0/authorizePage" + params);
                return false;
            }*/
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
