package com.ruoyi.area.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.mapper.AuthAccessTokenMapper;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * Access Token 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
@Service
public class AuthAccessTokenServiceImpl extends ServiceImpl<AuthAccessTokenMapper, AuthAccessToken> implements IAuthAccessTokenService {
    @Override
    public List<AuthAccessToken> selectList(AuthAccessToken authAccessToken) {
        QueryWrapper<AuthAccessToken> query = new QueryWrapper<>();
        // 查询条件

        return list(query);
    }

    @Override
    public AuthAccessToken selectByAccessToken(String accessToken) {
        QueryWrapper<AuthAccessToken> query = new QueryWrapper<>();
        query.lambda().eq(AuthAccessToken::getAccessToken, accessToken);
        return getOne(query);
    }

    @Override
    public AuthAccessToken selectByUserIdClientId(String userId, String clientId) {
        QueryWrapper<AuthAccessToken> query = new QueryWrapper<>();
        query.lambda().eq(AuthAccessToken::getUserId, userId);
        query.lambda().eq(AuthAccessToken::getClientId, clientId);
        return getOne(query);
    }
}
