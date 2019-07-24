package com.ruoyi.framework.jwt.service;



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ruoyi.framework.jwt.domain.Account;
import org.springframework.stereotype.Service;

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
    public String getToken(Account account) {
        String token="";
        token= JWT.create().withAudience(account.getId())// 将 user id 保存到 token 里面
                .sign(Algorithm.HMAC256(account.getPassword()));// 以 password 作为 token 的密钥
        return token;
    }
}