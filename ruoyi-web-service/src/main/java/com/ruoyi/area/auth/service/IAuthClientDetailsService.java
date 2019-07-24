package com.ruoyi.area.auth.service;

import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 接入的客户端 服务层
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
public interface IAuthClientDetailsService extends IService<AuthClientDetails> {
    List<AuthClientDetails> selectList(AuthClientDetails authClientDetails);

    /**
     * 通过clientId查询接入的客户端详情
     * @param clientId
     * @return
     */
    AuthClientDetails selectByClientId(String clientId);
}
