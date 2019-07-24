package com.ruoyi.service;

import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.system.domain.AuthRefreshToken;
import com.ruoyi.system.domain.SysUser;

/**
 * 授权相关Service
 *
 *
 * @author tao.liang
 * @date 2019/7/24
 */
public interface AuthorizationService {

    /**
     * 注册需要接入的客户端信息
     * @param clientDetails
     * @return
     */
    // boolean register(AuthClientDetails clientDetails);

    /**
     * 根据clientId、scope以及当前时间戳生成AuthorizationCode（有效期为10分钟）
     * @param clientIdStr
     * @param scopeStr
     * @param user
     * @return
     */
    String createAuthorizationCode(String clientIdStr, String scopeStr, SysUser user);

    /**
     * 生成Access Token
     * @param user
     * @param savedClientDetails
     * @param grantType
     * @param expiresIn
     * @return
     */
    String createAccessToken(SysUser user, AuthClientDetails savedClientDetails, String grantType, Long expiresIn);


    /**
     * 生成Refresh Token
     * @param user
     * @param authAccessToken
     * @return
     */
    String createRefreshToken(SysUser user, AuthAccessToken authAccessToken);
}
