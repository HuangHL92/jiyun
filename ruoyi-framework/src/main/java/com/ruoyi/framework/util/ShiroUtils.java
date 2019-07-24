package com.ruoyi.framework.util;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.shiro.realm.UserRealm;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

/**
 * shiro 工具类
 * 
 * @author ruoyi
 */
public class ShiroUtils
{

    private static CacheUtils cacheUtils = SpringUtils.getBean(CacheUtils.class);
    private static ISysUserService userService = SpringUtils.getBean(ISysUserService.class);

    public static Subject getSubject()
    {
        return SecurityUtils.getSubject();
    }

    public static Session getSession()
    {
        return SecurityUtils.getSubject().getSession();
    }

    public static void logout()
    {
        getSubject().logout();
    }

    public static SysUser getSysUser(String loginName)
    {
        SysUser user = null;
        if (StringUtils.isNotNull(loginName))
        {
            user = cacheUtils.getUserCache().get(loginName);
            if (user == null) {
                user = userService.selectUserByLoginName(loginName);
                cacheUtils.getUserCache().put(loginName, user);
            }
        }
        return user;
    }

    public static SysUser getSysUser()
    {
        String loginName = getLoginName();
        SysUser user = getSysUser(loginName);
        return user;
    }

    public static void setSysUser(SysUser user)
    {
//        Subject subject = getSubject();
//        PrincipalCollection principalCollection = subject.getPrincipals();
//        String realmName = principalCollection.getRealmNames().iterator().next();
//        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(user.getLoginName(), realmName);
        // 重新加载Principal
//        subject.runAs(newPrincipalCollection);
        // 重置用户缓存
        cacheUtils.getUserCache().put(user.getLoginName(), user);
    }

    public static void clearCachedAuthorizationInfo()
    {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm realm = (UserRealm) rsm.getRealms().iterator().next();
        realm.clearAllCachedAuthorizationInfo();
        cacheUtils.getUserMenuCache().clear();
    }

    public static String getUserId()
    {
        return getSysUser((String)SecurityUtils.getSubject().getPrincipal()).getUserId();
    }

    public static String getLoginName()
    {
        return (String)SecurityUtils.getSubject().getPrincipal();
    }

    public static String getIp()
    {
        return getSubject().getSession().getHost();
    }

    public static String getSessionId()
    {
        return String.valueOf(getSubject().getSession().getId());
    }

    /**
     * 生成随机盐
     */
    public static String randomSalt()
    {
        // 一个Byte占两个字节，此处生成的3字节，字符串长度为6
        SecureRandomNumberGenerator secureRandom = new SecureRandomNumberGenerator();
        String hex = secureRandom.nextBytes(3).toHex();
        return hex;
    }
}
