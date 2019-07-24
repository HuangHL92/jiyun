/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.ruoyi.framework.util;


import com.ruoyi.common.base.TokenEntity;
import com.ruoyi.system.domain.*;
import lombok.Getter;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cache工具类
 * @author jeeplus
 * @version 2017-1-19
 */
@Component
@Getter
public class CacheUtils {


	public static String getSPRING_REDIS_PREFIX() {
		return SPRING_REDIS_PREFIX;
	}

	@Value("${spring.redis.prefix}")
	public  void setSPRING_REDIS_PREFIX(String springRedisPrefix) {
		SPRING_REDIS_PREFIX = springRedisPrefix + ":";
	}

	/**
	 * Redis缓存前缀，用于区分不同的项目
	 */

	private static  String SPRING_REDIS_PREFIX;

	/**
	 * session缓存
	 */
	public static  String SHIRO_ACTIVE_SESSION_CACHE =  "shiro_redis_session";

	/**
	 * 登录记录缓存
	 */
	public static  String LOGIN_RECORD_CACHE =  "loginRecordCache";

	/**
	 * shiro 认证缓存
	 */
	public static  String AUTHENTICATION_CACHE =  "authenticationcache";

	/**
	 * shiro 授权缓存
	 */
	public static  String AUTHORIZATION_CACHE =  "authorizationcache";

	/**
	 * 用户 缓存
	 */
	public static  String USER_CACHE =  "userCache";

	/**
	 * token 缓存
	 */
	public static  String TOKEN_CACHE = "tokenCache";

	/**
	 * 菜单 缓存
	 */
	public static  String USER_MENU_CACHE = "userMenuCache";

	/**
	 * 字典 缓存
	 */
	public static  String DICT_CACHE = "dictCache";

    /**
     * 配置 缓存
     */
    public static  String CONFIG_CACHE = "configCache";

    /**
     * 用户在线 缓存
     */
    public static  String USER_ONLINE_CACHE = "userOnlineCache";


	private CacheManager cacheManager;

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	private Cache<String, AtomicInteger> loginRecordCache;

	private Cache<String, SysUser> userCache;

	private Cache<String, TokenEntity> tokenCache;

	private Cache<String, List<SysMenu>> userMenuCache;

	private Cache<String, List<SysDictData>> dictCache;

    private Cache<String, String> configCache;

    private Cache<String, List<SysUserOnline>> userOnlineCache;

	@PostConstruct
	public void init()
	{
		SHIRO_ACTIVE_SESSION_CACHE = SPRING_REDIS_PREFIX +SHIRO_ACTIVE_SESSION_CACHE;
		LOGIN_RECORD_CACHE= SPRING_REDIS_PREFIX +LOGIN_RECORD_CACHE;
		AUTHENTICATION_CACHE= SPRING_REDIS_PREFIX +AUTHENTICATION_CACHE;
		AUTHORIZATION_CACHE= SPRING_REDIS_PREFIX +AUTHORIZATION_CACHE;
		USER_CACHE = SPRING_REDIS_PREFIX +USER_CACHE;
		TOKEN_CACHE= SPRING_REDIS_PREFIX +TOKEN_CACHE;
        USER_MENU_CACHE= SPRING_REDIS_PREFIX +USER_MENU_CACHE;
		DICT_CACHE= SPRING_REDIS_PREFIX +DICT_CACHE;
        CONFIG_CACHE= SPRING_REDIS_PREFIX +CONFIG_CACHE;
        USER_ONLINE_CACHE= SPRING_REDIS_PREFIX +USER_ONLINE_CACHE;

		loginRecordCache = cacheManager.getCache(LOGIN_RECORD_CACHE);
		userCache = cacheManager.getCache(USER_CACHE);
		tokenCache = cacheManager.getCache(TOKEN_CACHE);
        userMenuCache = cacheManager.getCache(USER_MENU_CACHE);
		dictCache = cacheManager.getCache(DICT_CACHE);
        configCache = cacheManager.getCache(CONFIG_CACHE);
        userOnlineCache = cacheManager.getCache(USER_ONLINE_CACHE);
	}


}
