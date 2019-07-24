package com.ruoyi.api;

import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.area.auth.service.IAuthRefreshTokenService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.AuthConstants;
import com.ruoyi.common.base.ApiResult;
import com.ruoyi.common.enums.ExpireEnum;
import com.ruoyi.common.enums.GrantTypeEnum;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.redis.RedisService;
import com.ruoyi.service.AuthorizationService;
import com.ruoyi.system.domain.AuthRefreshToken;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于oauth2.0相关的授权相关操作
 *
 * @author tao.liang
 * @date 2019/7/24
 */
@Controller
@RequestMapping("/oauth2.0")
public class OauthController extends ApiBaseController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private IAuthClientDetailsService authClientDetailsService;
    @Autowired
    private IAuthAccessTokenService authAccessTokenService;
    @Autowired
    private IAuthRefreshTokenService authRefreshTokenService;
    @Autowired
    private ISysUserService userService;


    /**
     * 授权页面
     *
     * @return org.springframework.web.servlet.ModelAndView
     * @author zifangsky
     * @date 2018/8/3 16:31
     * @since 1.0.0
     */
    @RequestMapping("/authorizePage")
    public ModelAndView authorizePage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("authorize");

        //在页面同意授权后的回调地址
        String redirectUrl = request.getParameter("redirectUri");
        //客户端ID
        String clientId = request.getParameter("client_id");
        //权限范围
        String scope = request.getParameter("scope");

        if (StringUtils.isNoneBlank(redirectUrl)) {
            HttpSession session = request.getSession();
            //将回调地址添加到session中
            session.setAttribute(AuthConstants.SESSION_AUTH_REDIRECT_URL, redirectUrl);
        }

        //查询请求授权的客户端信息
        AuthClientDetails clientDetails = authClientDetailsService.selectByClientId(clientId);
        modelAndView.addObject("clientId", clientId);
        modelAndView.addObject("clientName", clientDetails.getClientName());
        modelAndView.addObject("scope", scope);

        return modelAndView;
    }

    /**
     * 获取Authorization Code
     *
     * @param request HttpServletRequest
     * @return org.springframework.web.servlet.ModelAndView
     * @author zifangsky
     * @date 2018/8/6 17:40
     * @since 1.0.0
     */
    @RequestMapping("/authorize")
    public ModelAndView authorize(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute(AuthConstants.SESSION_USER);

        //客户端ID
        String clientIdStr = request.getParameter("client_id");
        //权限范围
        String scopeStr = request.getParameter("scope");
        //回调URL
        String redirectUri = request.getParameter("redirect_uri");
        //status，用于防止CSRF攻击（非必填）
        String status = request.getParameter("status");

        //生成Authorization Code
        String authorizationCode = authorizationService.createAuthorizationCode(clientIdStr, scopeStr, user);

        String params = "?code=" + authorizationCode;
        if (StringUtils.isNoneBlank(status)) {
            params = params + "&status=" + status;
        }

        return new ModelAndView("redirect:" + redirectUri + params);
    }

    /**
     * 通过Authorization Code获取Access Token
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ApiResult token(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(8);

        //授权方式
        String grantType = request.getParameter("grant_type");
        //前面获取的Authorization Code
        String code = request.getParameter("code");
        //客户端ID
        String clientIdStr = request.getParameter("client_id");
        //接入的客户端的密钥
        String clientSecret = request.getParameter("client_secret");
        //回调URL
        String redirectUri = request.getParameter("redirect_uri");

        //校验授权方式
        if (!GrantTypeEnum.AUTHORIZATION_CODE.getType().equals(grantType)) {
            return error(ResponseCode.UNSUPPORTED_GRANT_TYPE);
        }

        try {
            AuthClientDetails savedClientDetails = authClientDetailsService.selectByClientId(clientIdStr);
            //校验请求的客户端秘钥和已保存的秘钥是否匹配
            if (!(savedClientDetails != null && savedClientDetails.getClientSecret().equals(clientSecret))) {
                return error(ResponseCode.INVALID_CLIENT);
            }

            //校验回调URL
            if (!savedClientDetails.getRedirectUri().equals(redirectUri)) {
                return error(ResponseCode.REDIRECT_URI_MISMATCH);
            }

            //从Redis获取允许访问的用户权限范围
            String scope = redisService.getObj(code + ":scope");
            //从Redis获取对应的用户信息
            SysUser user = redisService.getObj(code + ":user");

            //如果能够通过Authorization Code获取到对应的用户信息，则说明该Authorization Code有效
            if (StringUtils.isNoneBlank(scope) && user != null) {
                //过期时间
                Long expiresIn = DateUtils.dayToSecond(ExpireEnum.ACCESS_TOKEN.getTime());

                //生成Access Token
                String accessTokenStr = authorizationService.createAccessToken(user, savedClientDetails, grantType, expiresIn);
                //查询已经插入到数据库的Access Token
                AuthAccessToken authAccessToken = authAccessTokenService.selectByAccessToken(accessTokenStr);
                //生成Refresh Token
                String refreshTokenStr = authorizationService.createRefreshToken(user, authAccessToken);

                //返回数据
                result.put("access_token", authAccessToken.getAccessToken());
                result.put("refresh_token", refreshTokenStr);
                result.put("expires_in", expiresIn);
                return success(result);
            } else {
                return error(ResponseCode.INVALID_GRANT);
            }
        } catch (Exception e) {
            return error(ResponseCode.UNKNOWN_ERROR);
        }
    }

    /**
     * 通过Refresh Token刷新Access Token
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/refreshToken", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ApiResult refreshToken(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(8);

        //获取Refresh Token
        String refreshTokenStr = request.getParameter("refresh_token");

        try {
            AuthRefreshToken authRefreshToken = authRefreshTokenService.selectByRefreshToken(refreshTokenStr);

            if (authRefreshToken != null) {
                Long savedExpiresAt = authRefreshToken.getExpiresIn();
                //过期日期
                LocalDateTime expiresDateTime = DateUtils.ofEpochSecond(savedExpiresAt, null);
                //当前日期
                LocalDateTime nowDateTime = DateUtils.now();

                //如果Refresh Token已经失效，则需要重新生成
                if (expiresDateTime.isBefore(nowDateTime)) {
                    return error(ResponseCode.EXPIRED_TOKEN);
                } else {
                    //获取存储的Access Token
                    AuthAccessToken authAccessToken = authAccessTokenService.getById(authRefreshToken.getTokenId());
                    //获取对应的客户端信息
                    AuthClientDetails savedClientDetails = authClientDetailsService.selectByClientId(authAccessToken.getClientId());
                    //获取对应的用户信息
                    SysUser user = userService.selectUserById(authAccessToken.getUserId());

                    //新的过期时间
                    Long expiresIn = DateUtils.dayToSecond(ExpireEnum.ACCESS_TOKEN.getTime());
                    //生成新的Access Token
                    String newAccessTokenStr = authorizationService.createAccessToken(user, savedClientDetails, authAccessToken.getGrantType(), expiresIn);

                    //返回数据
                    result.put("access_token", newAccessTokenStr);
                    result.put("refresh_token", refreshTokenStr);
                    result.put("expires_in", expiresIn);
                    return success(result);
                }
            } else {
                return error(ResponseCode.INVALID_GRANT);
            }
        } catch (Exception e) {
            return error(ResponseCode.UNKNOWN_ERROR);
        }
    }

}
