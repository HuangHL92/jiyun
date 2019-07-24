package com.ruoyi.area.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.mapper.AuthClientDetailsMapper;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 接入的客户端 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
@Service
public class AuthClientDetailsServiceImpl extends ServiceImpl<AuthClientDetailsMapper, AuthClientDetails> implements IAuthClientDetailsService {
    @Override
    public List<AuthClientDetails> selectList(AuthClientDetails authClientDetails) {
        QueryWrapper<AuthClientDetails> query = new QueryWrapper<>();
        // 查询条件

        return list(query);
    }

    @Override
    public AuthClientDetails selectByClientId(String clientId) {
        QueryWrapper<AuthClientDetails> query = new QueryWrapper<>();
        query.lambda().eq(AuthClientDetails::getClientId, clientId);
        return getOne(query);
    }
}
