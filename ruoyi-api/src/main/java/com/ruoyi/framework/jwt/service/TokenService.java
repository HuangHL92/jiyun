package com.ruoyi.framework.jwt.service;



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.exception.ApiRuntimeException;
import com.ruoyi.framework.jwt.domain.Account;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description $功能描述$
 * @Author yufei
 * @Date 2019-03-08 10:37
 **/
@Service("TokenService")
public class TokenService {

    /**
     * 获得Token
     * @param account
     * @return
     */
    public String getToken(Account account, String appsecret, int expires) {

        Date date = new Date(System.currentTimeMillis() + expires*60*1000l);
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());

        String token="";

        token= JWT.create().withAudience(account.getId())// 将 user id 保存到 token 里面
                .withClaim("CURRENT_TIME_MILLIS", currentTimeMillis)
                .withExpiresAt(date)
                //TODO 密码用于验证token是否有效。可以提供两种实现方式（1：全系统用一个密码 2：跟着每个账号走）
                .sign(Algorithm.HMAC256(account.getPassword()));// 以 password 作为 token 的密钥
                //.sign(Algorithm.HMAC256(account.getPassword()));// 以 password 作为 token 的密钥

        return token;
    }
}