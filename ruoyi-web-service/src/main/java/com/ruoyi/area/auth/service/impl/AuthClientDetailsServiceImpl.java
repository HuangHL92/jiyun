package com.ruoyi.area.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.mapper.AuthClientDetailsMapper;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.common.utils.EncryptUtils;
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
        // 查询条件
        QueryWrapper<AuthClientDetails> query = new QueryWrapper<>();
        // 接入方式
        query.lambda().like(StrUtil.isNotEmpty(authClientDetails.getScope()), AuthClientDetails::getScope, authClientDetails.getScope());
        // 状态
        query.lambda().eq(authClientDetails.getStatus() != null, AuthClientDetails::getStatus, authClientDetails.getStatus());
        // 关键字：客户端ID/客户端名称/描述信息
        String keyword = authClientDetails.getParams().isEmpty() ? null : authClientDetails.getParams().get("keyword").toString();
        if (StrUtil.isNotBlank(keyword)) {
            query.lambda().and(i -> i.like(AuthClientDetails::getClientId, keyword)
                    .or().like(AuthClientDetails::getClientName, keyword)
                    .or().like(AuthClientDetails::getDescription, keyword));
        }
        return list(query);
    }

    @Override
    public AuthClientDetails selectByClientId(String clientId) {
        QueryWrapper<AuthClientDetails> query = new QueryWrapper<>();
        query.lambda().eq(AuthClientDetails::getClientId, clientId);
        return getOne(query);
    }

    @Override
    public boolean doSave(AuthClientDetails clientDetails) {
        boolean result;
        // 新增
        if (StrUtil.isEmpty(clientDetails.getId())) {
            //生成24位随机的clientId
            String clientId = EncryptUtils.getRandomStr1(24);
            //生成32位随机的clientSecret
            String clientSecret = EncryptUtils.getRandomStr1(32);
            clientDetails.setClientId(clientId);
            clientDetails.setClientSecret(clientSecret);
            result = save(clientDetails);
        }
        // 修改
        else {
            result = saveOrUpdate(clientDetails);
        }
        return result;
    }
}
