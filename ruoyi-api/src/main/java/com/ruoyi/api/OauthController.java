package com.ruoyi.api;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.area.auth.service.IAuthRefreshTokenService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.AuthConstants;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

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
@ApiIgnore()
@Controller
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
     * 获取Authorization Code
     * @param request
     * @return
     */
    @GetMapping("/auth")
    public ModelAndView auth(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute(AuthConstants.SESSION_USER);

        //客户端ID
        String clientIdStr = request.getParameter("client_id");
        //回调URL
        String redirectUri = request.getParameter("redirect_uri");
        //status，用于防止CSRF攻击（非必填）
        String status = request.getParameter("status");

        //生成Authorization Code
        String authorizationCode = authorizationService.createAuthorizationCode(clientIdStr, user);

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
    @PostMapping(value = "/access_token")
    @ResponseBody
    public Map<String, Object> accessToken(HttpServletRequest request) {
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
            generateErrorResponse(result, ResponseCode.UNSUPPORTED_GRANT_TYPE);
            return result;
        }

        try {
            AuthClientDetails savedClientDetails = authClientDetailsService.selectByClientId(clientIdStr);
            //校验请求的客户端秘钥和已保存的秘钥是否匹配
            if (!(savedClientDetails != null && savedClientDetails.getClientSecret().equals(clientSecret))) {
                generateErrorResponse(result, ResponseCode.INVALID_CLIENT);
                return result;
            }

            //校验回调URL
            if (!savedClientDetails.getRedirectUri().equals(redirectUri)) {
                generateErrorResponse(result, ResponseCode.REDIRECT_URI_MISMATCH);
                return result;
            }

            //从Redis获取对应的用户信息
            SysUser user = redisService.getObj(code + ":user");

            //如果能够通过Authorization Code获取到对应的用户信息，则说明该Authorization Code有效
            if (user != null) {
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
                return result;
            } else {
                generateErrorResponse(result, ResponseCode.INVALID_GRANT);
                return result;
            }
        } catch (Exception e) {
            generateErrorResponse(result, ResponseCode.UNKNOWN_ERROR);
            return result;
        }
    }

    /**
     * 通过Refresh Token刷新Access Token
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/refresh_token")
    @ResponseBody
    public Map<String, Object> refreshToken(HttpServletRequest request) {
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
                    generateErrorResponse(result, ResponseCode.EXPIRED_TOKEN);
                    return result;
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
                    return result;
                }
            } else {
                generateErrorResponse(result, ResponseCode.INVALID_GRANT);
                return result;
            }
        } catch (Exception e) {
            generateErrorResponse(result, ResponseCode.UNKNOWN_ERROR);
            return result;
        }
    }

    /**
     * 密码模式获取Access Token
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/auth_pw", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Map<String, Object> authPw(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(2);

        // 用户账号
        String username = request.getParameter("username");
        // 用户密码
        String password = request.getParameter("password");
        // 客户端ID
        String clientId = request.getParameter("client_id");
        // 接入的客户端的密钥
        String clientSecret = request.getParameter("client_secret");

        // 1.参数验证
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password) || StrUtil.isBlank(clientId) || StrUtil.isBlank(clientSecret)) {
            generateErrorResponse(result, ResponseCode.INVALID_REQUEST);
            return result;
        }

        try {
            // 2.验证账号密码
            Map<String, Object> checkMap = userService.checkLogin(username, password);
            Boolean loginResult = (Boolean) checkMap.get("result");
            SysUser user = (SysUser) checkMap.get("user");
            // 登录验证通过
            if (loginResult != null && loginResult) {
                // 3.校验请求的客户端秘钥和已保存的秘钥是否匹配
                AuthClientDetails savedClientDetails = authClientDetailsService.selectByClientId(clientId);
                if (savedClientDetails == null || !savedClientDetails.getClientSecret().equals(clientSecret)) {
                    return generateErrorResponse(ResponseCode.INVALID_CLIENT);
                }
                // 4.校验客户端是否支持password授权模式
                // TODO
                // 5.生成accessToken
                Long expiresIn = DateUtils.dayToSecond(ExpireEnum.ACCESS_TOKEN.getTime()); // 过期时间
                String accessToken = authorizationService.createAccessToken(user, savedClientDetails, "password", expiresIn);// 生成Access Token
                result.put("code", 200);
                result.put("access_token", accessToken);
                result.put("expires_in", expiresIn);
            } else {
                generateErrorResponse(result, ResponseCode.INVALID_USERNAME_PASSWORD);
            }
        } catch (Exception e) {
            generateErrorResponse(result, ResponseCode.UNKNOWN_ERROR);
        }
        return result;

    }

    /**
     * 主动跳转到系统
     * @param request
     * @return
     */
    @PostMapping("/auth_direct")
    @ResponseBody
    public Map<String, Object> authDirect(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(2);

        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute(AuthConstants.SESSION_USER);

        // 客户端ID
        String clientId = request.getParameter("client_id");
        AuthClientDetails authClientDetails = authClientDetailsService.selectByClientId(clientId);

        // TODO 需要判断当前用户是否有该系统的访问权限

        // 回调url
        String redirectUri = authClientDetails.getRedirectUri();

        //生成Authorization Code
        String authorizationCode = authorizationService.createAuthorizationCode(clientId, user);

        result.put("code", 200);
        result.put("redirect_uri", redirectUri + "?code=" + authorizationCode);

        return result;
    }
}
