package com.ruoyi.api;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.enums.ExpireEnum;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.service.AuthorizationService;
import com.ruoyi.service.PasswordService;
import com.ruoyi.system.domain.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于oauth2.0相关的授权相关操作
 *  password方式认证
 * @author tao.liang
 * @date 2019/7/24
 */
@Api(value = "/", description = "password方式认证", tags = "password认证")
@Controller
public class OauthPasswordController extends ApiBaseController {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private IAuthClientDetailsService authClientDetailsService;
    @Autowired
    private PasswordService passwordService;


    /**
     * 密码模式获取Access Token
     * @param username 用户账号
     * @param password 用户密码
     * @param clientId 客户端ID
     * @param clientSecret 接入的客户端的密钥
     * @param scope scope
     * @return
     */
    @ApiOperation(value = "获得access_token", notes = "此类方式仅开放给特定应用，且只允许特定信任服务器的递交信息。")
    @PostMapping(value = "/auth_pw")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户账号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "client_id", value = "申请的AppId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "client_secret", value = "申请的AppSecret", required = true, paramType = "query"),
            @ApiImplicitParam(name = "scope", value = "scope", paramType = "query")
    })
    public Map<String, Object> authPw(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      @RequestParam("client_id") String clientId,
                                      @RequestParam("client_secret") String clientSecret,
                                      @RequestParam(value = "scope", required = false) String scope) {
        Map<String, Object> result = new HashMap<>(2);

        // 1.参数验证
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password) || StrUtil.isBlank(clientId) || StrUtil.isBlank(clientSecret)) {
            generateErrorResponse(result, ResponseCode.INVALID_REQUEST);
            return result;
        }

        try {
            // 2.验证账号密码
            Map<String, Object> checkMap = passwordService.checkLogin(username, password);
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
                if (!savedClientDetails.getScope().contains(AuthClientDetails.AUTH_SCOPE_PASSWORD)) {
                    return generateErrorResponse(ResponseCode.INVALID_AUTH_SCOPE);
                }
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

}
