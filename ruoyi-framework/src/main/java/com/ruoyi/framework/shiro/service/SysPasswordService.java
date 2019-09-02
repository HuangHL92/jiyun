package com.ruoyi.framework.shiro.service;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;

import com.ruoyi.common.config.Global;
import com.ruoyi.common.exception.user.UserPasswordRetryLimitCountException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.CacheUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.exception.user.UserPasswordRetryLimitExceedException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.domain.SysUser;

/**
 * 登录密码方法
 * 
 * @author ruoyi
 */
@Component
public class SysPasswordService
{
//    @Autowired
//    private CacheManager cacheManager;
//
    @Autowired
    private CacheUtils cacheUtils;

//    private Cache<String, AtomicInteger> loginRecordCache = cacheUtils.getLoginRecordCache();

    @Value(value = "${user.password.maxRetryCount}")
    private String maxRetryCount;

//    @PostConstruct
//    public void init()
//    {
//        loginRecordCache = cacheManager.getCache("loginRecordCache");
//    }

    public void validate(SysUser user, String password)
    {
        String loginName = user.getLoginName();

        AtomicInteger retryCount = cacheUtils.getLoginRecordCache().get(loginName);

        if (retryCount == null)
        {
            retryCount = new AtomicInteger(0);
            cacheUtils.getLoginRecordCache().put(loginName, retryCount);
        }
        if (retryCount.incrementAndGet() >= Integer.valueOf(maxRetryCount).intValue())
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.exceed", maxRetryCount)));
            throw new UserPasswordRetryLimitExceedException(Integer.valueOf(maxRetryCount).intValue(), StringUtils.nvl(Global.getConfig("user.password.lockTime"), "10"));
        }

        if (!matches(user, password))
        {
            int retryRemainCount = Integer.valueOf(maxRetryCount) - retryCount.intValue();
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.count", retryCount, retryRemainCount)));
            cacheUtils.getLoginRecordCache().put(loginName, retryCount);
            throw new UserPasswordRetryLimitCountException(retryCount.intValue(), retryRemainCount);
        }
        else
        {
            clearLoginRecordCache(loginName);
        }
    }

    public boolean matches(SysUser user, String newPassword)
    {
        return user.getPassword().equals(encryptPassword(user.getLoginName(), newPassword, user.getSalt()));
    }

    public void clearLoginRecordCache(String username)
    {
        cacheUtils.getLoginRecordCache().remove(username);
    }

    public String encryptPassword(String username, String password, String salt)
    {
        return new Md5Hash(username + password + salt).toHex().toString();
    }

}
