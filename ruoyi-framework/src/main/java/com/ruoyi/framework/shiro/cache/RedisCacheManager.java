package com.ruoyi.framework.shiro.cache;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.redis.RedisManager;
import com.ruoyi.framework.util.CacheUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存管理器 使用redis实现
 * 
 * @author ruoyi
 */
//@Service
//@SuppressWarnings(value = { "unchecked", "rawtypes" })
public class RedisCacheManager implements CacheManager
{
    /**
     * 用于shiro中用到的cache
     */
    private ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    /**
     * redis cache 工具类
     */
    private RedisManager redisManager = SpringUtils.getBean(RedisManager.class);

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException
    {
        Cache<K, V> cache = caches.get(name);
        if (cache == null)
        {
            synchronized (this)
            {
                // 登录记录缓存：10分钟
                if (CacheUtils.LOGIN_RECORD_CACHE.equals(name))
                {
                    cache = new RedisCache<>(name, 600, redisManager);
                }
                // session缓存：30分钟
                else if (CacheUtils.SHIRO_ACTIVE_SESSION_CACHE.equals(name)) {
                    cache = new RedisCache<>(name, 1800, redisManager);
                }
                // 用户缓存：持久
                else if (CacheUtils.USER_CACHE.equals(name)) {
                    cache = new RedisCache<>(name, -1, redisManager);
                }
                //Token缓存：60分钟
                else if (CacheUtils.TOKEN_CACHE.equals(name)) {
                    cache = new RedisCache<>(name, 3600, redisManager);
                }
                // 菜单（授权缓存）：持久
                else if (CacheUtils.USER_MENU_CACHE.equals(name)) {
                    cache = new RedisCache<>(name, -1, redisManager);
                }
                // 字典（授权缓存）：持久
                else if (CacheUtils.DICT_CACHE.equals(name)) {
                    cache = new RedisCache<>(name, -1, redisManager);
                }
                // 用户在线缓存（实时刷新过期时间，默认 60分钟）
                else if (CacheUtils.USER_ONLINE_CACHE.equals(name)) {
                    cache = new RedisCache<>(name, 3600, redisManager);
                }
                // 默认缓存（授权缓存）：60分钟
                else {
                    cache = new RedisCache<>(name, 3600, redisManager);
                }
                caches.put(name, cache);
            }
        }
        return cache;
    }

}
