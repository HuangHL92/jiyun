package com.ruoyi.area.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.area.auth.mapper.AuthRefreshTokenMapper;
import com.ruoyi.area.auth.service.IAuthRefreshTokenService;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.AuthRefreshToken;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * Refresh Token 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
@Service
public class AuthRefreshTokenServiceImpl extends ServiceImpl<AuthRefreshTokenMapper, AuthRefreshToken> implements IAuthRefreshTokenService {
    @Override
    public List<AuthRefreshToken> selectList(AuthRefreshToken authRefreshToken) {
        QueryWrapper<AuthRefreshToken> query = new QueryWrapper<>();
        // 查询条件

        return list(query);
    }

    @Override
    public AuthRefreshToken selectByRefreshToken(String refreshTokenStr) {
        QueryWrapper<AuthRefreshToken> query = new QueryWrapper<>();
        query.lambda().eq(AuthRefreshToken::getRefreshToken, refreshTokenStr);
        return getOne(query);
    }
}
