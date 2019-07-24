package com.ruoyi.area.auth.service;

import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Access Token 服务层
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
public interface IAuthAccessTokenService extends IService<AuthAccessToken> {
    List<AuthAccessToken> selectList(AuthAccessToken authAccessToken);

    /**
     * 通过Access Token查询记录
     * @param accessToken
     * @return
     */
    AuthAccessToken selectByAccessToken(String accessToken);

    /**
     * 通过userId + clientId查询记录
     * @param userId
     * @param clientId
     * @return
     */
    AuthAccessToken selectByUserIdClientId(String userId, String clientId);
}
