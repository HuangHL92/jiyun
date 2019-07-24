package com.ruoyi.area.auth.service;

import com.ruoyi.system.domain.AuthRefreshToken;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Refresh Token 服务层
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
public interface IAuthRefreshTokenService extends IService<AuthRefreshToken> {
    List<AuthRefreshToken> selectList(AuthRefreshToken authRefreshToken);

    /**
     * 通过Refresh Token查询记录
     * @param refreshTokenStr
     * @return
     */
    AuthRefreshToken selectByRefreshToken(String refreshTokenStr);
}
