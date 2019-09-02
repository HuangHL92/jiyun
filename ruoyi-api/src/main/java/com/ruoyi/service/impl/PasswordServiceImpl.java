package com.ruoyi.service.impl;

import com.ruoyi.common.AuthConstants;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.exception.ApiRuntimeException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.exception.user.UserPasswordRetryLimitExceedException;
import com.ruoyi.framework.redis.RedisService;
import com.ruoyi.service.PasswordService;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zifangsky
 *
 * @author tao.liang
 * @date 2019/7/24
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private RedisService redisService;
    /**
     * 密码错误次数限制
     */
    @Value(value = "${user.password.maxRetryCount}")
    private Integer maxRetryCount;

    @Override
    public Map<String, Object> checkLogin(String username, String password) {
        //返回信息
        Map<String, Object> result = new HashMap<>(2);

        // 查询用户信息
        SysUser correctUser = userMapper.selectUserByLoginName(username);
        boolean flag = false;
        if (correctUser != null) {
            flag = validate(correctUser, password);
        }
        result.put("result", flag);
        result.put("user", correctUser);
        return result;
    }

    private boolean validate(SysUser user, String password) {
        // 重试次数的限定
        String retryCountLoginName = AuthConstants.RETRY_COUNT_PREFIX + user.getLoginName();
        AtomicInteger retryCount = redisService.getObj(retryCountLoginName);

        if (retryCount == null)
        {
            retryCount = new AtomicInteger(0);
            redisService.setWithExpire(retryCountLoginName, retryCount, 10, TimeUnit.MINUTES);
        }
        if (retryCount.incrementAndGet() > maxRetryCount)
        {
            // AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.exceed", maxRetryCount)));
            // throw new UserPasswordRetryLimitExceedException(maxRetryCount);
            throw new ApiRuntimeException(ResponseCode.PASSWORD_RETRY_LIMIT_EXCEED);
        }

        if (!matches(user, password)) {
             // AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.count", retryCount)));
            redisService.setWithExpire(retryCountLoginName, retryCount, 10, TimeUnit.MINUTES);
            // throw new UserPasswordNotMatchException();
            throw new ApiRuntimeException(ResponseCode.ERROR_LOGIN);
        } else {
            return true;
        }
    }

    private boolean matches(SysUser user, String newPassword) {
        return user.getPassword().equals(encryptPassword(user.getLoginName(), newPassword, user.getSalt()));
    }

    private String encryptPassword(String username, String password, String salt) {
        return new Md5Hash(username + password + salt).toHex().toString();
    }

}
