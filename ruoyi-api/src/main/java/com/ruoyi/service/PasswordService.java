package com.ruoyi.service;

import java.util.Map;

/**
 * 授权相关Service
 *
 *
 * @author tao.liang
 * @date 2019/7/24
 */
public interface PasswordService {

    /**
     * 验证登陆
     * @param username
     * @param password
     * @return
     */
    Map<String,Object> checkLogin(String username, String password);
}
