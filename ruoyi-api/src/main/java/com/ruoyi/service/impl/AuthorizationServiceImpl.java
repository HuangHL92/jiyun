package com.ruoyi.service.impl;

import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.area.auth.service.IAuthRefreshTokenService;
import com.ruoyi.common.enums.ExpireEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.redis.RedisService;
import com.ruoyi.service.AuthorizationService;
import com.ruoyi.system.domain.AuthRefreshToken;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.utils.EncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author zifangsky
 *
 * @author tao.liang
 * @date 2019/7/24
 */
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private IAuthAccessTokenService authAccessTokenService;
    @Autowired
    private IAuthRefreshTokenService authRefreshTokenService;

    /*@Override
    public boolean register(AuthClientDetails clientDetails) {
        //客户端的名称和回调地址不能为空
        if (StringUtils.isNoneBlank(clientDetails.getClientName()) && StringUtils.isNoneBlank(clientDetails.getRedirectUri())) {
            //生成24位随机的clientId
            String clientId = EncryptUtils.getRandomStr1(24);

            AuthClientDetails savedClientDetails = authClientDetailsMapper.selectByClientId(clientId);
            //生成的clientId必须是唯一的
            for (int i = 0; i < 10; i++) {
                if (savedClientDetails == null) {
                    break;
                } else {
                    clientId = EncryptUtils.getRandomStr1(24);
                    savedClientDetails = authClientDetailsMapper.selectByClientId(clientId);
                }
            }

            //生成32位随机的clientSecret
            String clientSecret = EncryptUtils.getRandomStr1(32);

            Date current = new Date();
            HttpSession session = SpringContextUtils.getSession();
            User user = (User) session.getAttribute(Constants.SESSION_USER);

            clientDetails.setClientId(clientId);
            clientDetails.setClientSecret(clientSecret);
            clientDetails.setCreateUser(user.getId());
            clientDetails.setCreateTime(current);
            clientDetails.setUpdateUser(user.getId());
            clientDetails.setUpdateTime(current);
            clientDetails.setStatus(1);

            //保存到数据库
            authClientDetailsMapper.insertSelective(clientDetails);

            return true;
        } else {
            return false;
        }
    }*/

    @Override
    public String createAuthorizationCode(String clientIdStr, String scopeStr, SysUser user) {
        //1. 拼装待加密字符串（clientId + scope + 当前精确到毫秒的时间戳）
        String str = clientIdStr + scopeStr + String.valueOf(DateUtils.currentTimeMillis());

        //2. SHA1加密
        String encryptedStr = EncryptUtils.sha1Hex(str);

        //3.1 保存本次请求的授权范围
        redisService.setWithExpire(encryptedStr + ":scope", scopeStr, (ExpireEnum.AUTHORIZATION_CODE.getTime()), ExpireEnum.AUTHORIZATION_CODE.getTimeUnit());
        //3.2 保存本次请求所属的用户信息
        redisService.setWithExpire(encryptedStr + ":user", user, (ExpireEnum.AUTHORIZATION_CODE.getTime()), ExpireEnum.AUTHORIZATION_CODE.getTimeUnit());

        //4. 返回Authorization Code
        return encryptedStr;
    }

    @Override
    public String createAccessToken(SysUser user, AuthClientDetails savedClientDetails, String grantType, Long expiresIn) {
        Date current = new Date();
        //过期的时间戳
        Long expiresAt = DateUtils.nextDaysSecond(ExpireEnum.ACCESS_TOKEN.getTime(), null);

        //1. 拼装待加密字符串（username + clientId + 当前精确到毫秒的时间戳）
        String str = user.getLoginName() + savedClientDetails.getClientId() + String.valueOf(DateUtils.currentTimeMillis());

        //2. SHA1加密
        String accessTokenStr = "1." + EncryptUtils.sha1Hex(str) + "." + expiresIn + "." + expiresAt;

        //3. 保存Access Token
        AuthAccessToken savedAccessToken = authAccessTokenService.selectByUserIdClientId(user.getUserId(), savedClientDetails.getId());
        //如果存在userId + clientId + scope匹配的记录，则更新原记录，否则向数据库中插入新记录
        if (savedAccessToken != null) {
            savedAccessToken.setAccessToken(accessTokenStr);
            savedAccessToken.setExpiresIn(expiresAt);
            savedAccessToken.setUpdateBy(user.getUserId());
            savedAccessToken.setUpdateTime(current);
            authAccessTokenService.updateById(savedAccessToken);
        } else {
            savedAccessToken = new AuthAccessToken();
            savedAccessToken.setAccessToken(accessTokenStr);
            savedAccessToken.setUserId(user.getUserId());
            savedAccessToken.setClientId(savedClientDetails.getId());
            savedAccessToken.setExpiresIn(expiresAt);
            savedAccessToken.setGrantType(grantType);
            savedAccessToken.setCreateBy(user.getUserId());
            savedAccessToken.setUpdateBy(user.getUserId());
            savedAccessToken.setCreateTime(current);
            savedAccessToken.setUpdateTime(current);
            authAccessTokenService.save(savedAccessToken);
        }

        //4. 返回Access Token
        return accessTokenStr;
    }

    @Override
    public String createRefreshToken(SysUser user, AuthAccessToken authAccessToken) {
        Date current = new Date();
        //过期时间
        Long expiresIn = DateUtils.dayToSecond(ExpireEnum.REFRESH_TOKEN.getTime());
        //过期的时间戳
        Long expiresAt = DateUtils.nextDaysSecond(ExpireEnum.REFRESH_TOKEN.getTime(), null);

        //1. 拼装待加密字符串（username + accessToken + 当前精确到毫秒的时间戳）
        String str = user.getLoginName() + authAccessToken.getAccessToken() + String.valueOf(DateUtils.currentTimeMillis());

        //2. SHA1加密
        String refreshTokenStr = "2." + EncryptUtils.sha1Hex(str) + "." + expiresIn + "." + expiresAt;

        //3. 保存Refresh Token
        AuthRefreshToken savedRefreshToken = authRefreshTokenService.getById(authAccessToken.getId());
        //如果存在tokenId匹配的记录，则更新原记录，否则向数据库中插入新记录
        if (savedRefreshToken != null) {
            savedRefreshToken.setRefreshToken(refreshTokenStr);
            savedRefreshToken.setExpiresIn(expiresAt);
            savedRefreshToken.setUpdateBy(user.getUserId());
            savedRefreshToken.setUpdateTime(current);
            authRefreshTokenService.updateById(savedRefreshToken);
        } else {
            savedRefreshToken = new AuthRefreshToken();
            savedRefreshToken.setTokenId(authAccessToken.getId());
            savedRefreshToken.setRefreshToken(refreshTokenStr);
            savedRefreshToken.setExpiresIn(expiresAt);
            savedRefreshToken.setCreateBy(user.getUserId());
            savedRefreshToken.setUpdateBy(user.getUserId());
            savedRefreshToken.setCreateTime(current);
            savedRefreshToken.setUpdateTime(current);
            authRefreshTokenService.save(savedRefreshToken);
        }

        //4. 返回Refresh Token
        return refreshTokenStr;
    }

}
