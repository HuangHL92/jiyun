package com.ruoyi.framework.shiro.cache;

import com.ruoyi.framework.redis.RedisManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 使用redis实现Cache缓存
 * 
 * @author ruoyi
 */
@SuppressWarnings("unchecked")
public class RedisCache<K, V> implements Cache<K, V>
{
    /**
     * 缓存的超时时间，单位为s
     */
    private long expireTime;

    /**
     * 用于shiro的cache的名字
     */
    private String cacheName;

    private RedisManager redisManager;

    public RedisCache(String cacheName, long expireTime, RedisManager redisManager)
    {
        super();
        this.cacheName = cacheName;
        this.expireTime = expireTime;
        this.redisManager = redisManager;
    }

    public String cacheKey(String cacheName, K key)
    {
        return keyPrefix(cacheName) + key;
    }

    public String keyPrefix(String cacheName)
    {
        return cacheName + ":";
    }

    /**
     * 通过key来获取对应的缓存对象
     */
    @Override
    public V get(K key) throws CacheException
    {
        if (key == null) {
            return null;
        }

        try {
            String redisCacheKey = cacheKey(cacheName, key);
            Object rawValue = redisManager.get(redisCacheKey);
            if (rawValue == null) {
                return null;
            }
            V value = (V) rawValue;
            return value;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * 将权限信息加入Redis缓存中
     */
    @Override
    public V put(K key, V value) throws CacheException
    {
        if (key == null) {
            return value;
        }
        try {
            String redisCacheKey = cacheKey(cacheName, key);
            redisManager.set(redisCacheKey, value != null ? value : null, expireTime);
            return value;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * 将权限信息从缓存中删除
     */
    @Override
    public V remove(K key) throws CacheException
    {
        if (key == null) {
            return null;
        }
        try {
            String redisCacheKey = cacheKey(cacheName, key);
            Object rawValue = redisManager.get(redisCacheKey);
            V previous = (V) rawValue;
            redisManager.del(redisCacheKey);
            return previous;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void clear() throws CacheException
    {
        Set<String> keys = null;
        try {
            keys = redisManager.scan(keyPrefix(cacheName) + "*");
        } catch (Exception e) {
        }
        if (keys == null || keys.size() == 0) {
            return;
        }
        for (String key: keys) {
            redisManager.del(key);
        }
    }

    @Override
    public int size()
    {
        Long longSize = 0L;
        try {
            longSize = new Long(redisManager.scanSize(keyPrefix(cacheName) + "*"));
        } catch (Exception e) {
        }
        return longSize.intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        Set<String> keys = null;
        try {
            keys = redisManager.scan(keyPrefix(cacheName) + "*");
        } catch (Exception e) {
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        Set<K> convertedKeys = new HashSet<K>();
        for (String key:keys) {
            try {
                convertedKeys.add((K) key);
            } catch (Exception e) {
            }
        }
        return convertedKeys;
    }

    @Override
    public Collection<V> values() {
        Set<String> keys = null;
        try {
            keys = redisManager.scan(keyPrefix(cacheName) + "*");
        } catch (Exception e) {
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        List<V> values = new ArrayList<V>(keys.size());
        for (String key : keys) {
            V value = null;
            try {
                value = (V) redisManager.get(key);
            } catch (Exception e) {
            }
            if (value != null) {
                values.add(value);
            }
        }
        return Collections.unmodifiableList(values);
    }

}
