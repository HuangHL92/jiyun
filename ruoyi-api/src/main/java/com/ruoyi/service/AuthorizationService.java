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
     * 根据clientId以及当前时间戳生成AuthorizationCode（有效期为10分钟）
     * @param clientIdStr
     * @param user
     * @return
     */
    String createAuthorizationCode(String clientIdStr, SysUser user);

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
