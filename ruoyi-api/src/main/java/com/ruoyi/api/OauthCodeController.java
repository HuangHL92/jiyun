package com.ruoyi.api;

import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.area.auth.service.IAuthRefreshTokenService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.AuthConstants;
import com.ruoyi.common.SpringContextUtils;
import com.ruoyi.common.enums.ExpireEnum;
import com.ruoyi.common.enums.GrantTypeEnum;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.redis.RedisService;
import com.ruoyi.service.AuthorizationService;
import com.ruoyi.system.domain.AuthRefreshToken;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于oauth2.0相关的授权相关操作
 *  Code方式授权认证接口
 *
 * @author tao.liang
 * @date 2019/7/24
 */
@Api(value = "/", description = "Code方式授权认证接口", tags = "code认证", hidden = true)
@Controller
public class OauthCodeController extends ApiBaseController {

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
     * @param clientIdStr 客户端ID
     * @param redirectUri 回调URL
     * @param status status，用于防止CSRF攻击（非必填）
     * @return
     */
    @ApiOperation(value = "1、跳转登录页面", tags = "code认证", notes = "上述操作将跳转到EIcjs OAuth2 登陆页面。\n" +
            "如果用户登陆成功并确认同意，则将跳转到递交的 redirect_uri 并带有返回码。")
    @GetMapping("/auth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "response_type", value = "code", required = true, paramType = "query"),
            @ApiImplicitParam(name = "client_id", value = "申请的AppId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "redirect_uri", value = "申请AppRedirectUri(返回到客户的url)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "status，用于防止CSRF攻击（非必填）", paramType = "query")
    })
    public String auth(@RequestParam("client_id") String clientIdStr,
                       @RequestParam("redirect_uri") String redirectUri,
                       @RequestParam(value = "status", required = false) String status) {
        HttpSession session = SpringContextUtils.getSession();
        SysUser user = (SysUser) session.getAttribute(AuthConstants.SESSION_USER);

        //生成Authorization Code
        String authorizationCode = authorizationService.createAuthorizationCode(clientIdStr, user);

        String params = "?code=" + authorizationCode;
        if (StringUtils.isNoneBlank(status)) {
            params = params + "&status=" + status;
        }

        return "redirect:" + redirectUri + params;
    }

    /**
     * 通过Authorization Code获取Access Token
     * @param grantType 授权方式
     * @param clientIdStr 客户端ID
     * @param clientSecret 接入的客户端的密钥
     * @param redirectUri 回调URL
     * @param code 前面获取的Authorization Code
     * @return
     */
    @ApiOperation(value = "2、获取access_token", tags = "code认证", notes = "使用返回的code，获取认证服务器的 access_token")
    @PostMapping(value = "/access_token")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grant_type", value = "authorization_code", required = true, paramType = "query"),
            @ApiImplicitParam(name = "client_id", value = "申请的AppId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "client_secret", value = "申请的AppSecret", required = true, paramType = "query"),
            @ApiImplicitParam(name = "redirect_uri", value = "申请AppRedirectUri(返回到客户的url)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "code", value = "前一步获取到的 code", required = true, paramType = "query")
    })
    public Map<String, Object> access_token(@RequestParam("grant_type") String grantType,
                                            @RequestParam("client_id") String clientIdStr,
                                            @RequestParam("client_secret") String clientSecret,
                                            @RequestParam("redirect_uri") String redirectUri,
                                            @RequestParam("code") String code) {
        Map<String, Object> result = new HashMap<>(8);

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
     * @param grantType 授权模式
     * @param refreshToken 之前获取到的 refresh_token
     * @return
     */
    @ApiOperation(value = "3、刷新access_token", tags = "code认证",
            notes = "对于已经获得用户认证过的应用， 当用户再次访问时， 可以通过本地 session 获取其用户之前获得的 refresh_token 再次获取 access_token (如果超时，请用户重新从第一步开始认证)。\n" +
                    "对于已经获得用户认证的应用， 建议调用用户的唯一ID号(参考 API)以及结合本地应用session来实现对应用中的用户进行判断。")
    @PostMapping(value = "/refresh_token")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grant_type", value = "refresh_token", required = true, paramType = "query"),
            @ApiImplicitParam(name = "refresh_token", value = "之前获取到的 refresh_token", required = true, paramType = "query")
    })
    public Map<String, Object> refresh_token(@RequestParam("grant_type") String grantType,
                                             @RequestParam("refresh_token") String refreshToken) {
        Map<String, Object> result = new HashMap<>(8);

        //校验授权方式
        if (!GrantTypeEnum.AUTHORIZATION_PASSWORD.getType().equals(grantType)) {
            generateErrorResponse(result, ResponseCode.UNSUPPORTED_GRANT_TYPE);
            return result;
        }

        try {
            AuthRefreshToken authRefreshToken = authRefreshTokenService.selectByRefreshToken(refreshToken);

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
                    String newAccessTokenStr = authorizationService.createAccessToken(user, savedClientDetails, grantType, expiresIn);

                    //返回数据
                    result.put("access_token", newAccessTokenStr);
                    result.put("refresh_token", refreshToken);
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
}
