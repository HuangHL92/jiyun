package com.ruoyi.common.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jedis Cache 工具类
 *
 * @author tao.liang
 * @version 2019-02-19
 */
@Component
public class JedisUtils {

    private static Logger logger = LoggerFactory.getLogger(JedisUtils.class);


    public String getPrefix() {
        return prefix;
    }

    @Value("${spring.redis.prefix}")
    public void setPrefix(String pre) {
        prefix = pre+":";
    }


    private static String prefix;



    private static JedisPool jedisPool = SpringUtils.getBean(JedisPool.class);


    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static  String get(String key) {
        String value = null;
        key = prefix+ key;
        Jedis jedis = getJedis();
        try{
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                logger.debug("get {} = {}", key, value);
            }
        } finally {
            //释放资源
            returnResource(jedis);
        }


        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static  Object getObject(String key) {
        Object value = null;
        Jedis jedis = getJedis();

        try{
            key = prefix+ key;
            if (jedis.exists(getBytesKey(key))) {
                value = toObject(jedis.get(getBytesKey(key)));
                logger.debug("getObject {} = {}", key, value);
            }
        } finally {
            //释放资源
            returnResource(jedis);
        }



        return value;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  String set(String key, String value, int cacheSeconds) {
        String result = null;
        Jedis jedis = getJedis();
        try {
            key = prefix+ key;
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  String setObject(String key, Object value, int cacheSeconds) {
        String result = null;
        Jedis jedis = getJedis();
        try{
            key = prefix+ key;
            result = jedis.set(getBytesKey(key), toBytes(value));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObject {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public static  List<String> getList(String key) {
        List<String> value = null;
        Jedis jedis = getJedis();

        try{
            key = prefix+ key;
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
                logger.debug("getList {} = {}", key, value);
            }
        } finally {
            //释放资源
            returnResource(jedis);
        }



        return value;
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public static  List<Object> getObjectList(String key) {
        List<Object> value = null;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(getBytesKey(key))) {
                List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
                value = Lists.newArrayList();
                for (byte[] bs : list) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectList {} = {}", key, value);
            }
        } finally {
            //释放资源
            returnResource(jedis);
        }

        return value;
    }


    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.rpush(key, (String[]) value.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setList {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectList {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static  long listAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;
        try{
            result = jedis.rpush(key, value);
            logger.debug("listAdd {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static  long listObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;
        try{
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            logger.debug("listObjectAdd {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static  Set<String> getSet(String key) {
        Set<String> value = null;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(key)) {
                value = jedis.smembers(key);
                logger.debug("getSet {} = {}", key, value);
            }
        } finally {
            //释放资源
            returnResource(jedis);
        }

        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static  Set<Object> getObjectSet(String key) {
        Set<Object> value = null;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(getBytesKey(key))) {
                value = Sets.newHashSet();
                Set<byte[]> set = jedis.smembers(getBytesKey(key));
                for (byte[] bs : set) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectSet {} = {}", key, value);
            }
        } finally {
            //释放资源
            returnResource(jedis);
        }



        return value;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;


        try{
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.sadd(key, (String[]) value.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setSet {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectSet {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static  long setSetAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            result = jedis.sadd(key, value);
            logger.debug("setSetAdd {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static  long setSetObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
            logger.debug("setSetObjectAdd {} = {}", key, value);
        } finally {
            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public static  Map<String, String> getMap(String key) {
        Map<String, String> value = null;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(key)) {
                value = jedis.hgetAll(key);
                logger.debug("getMap {} = {}", key, value);
            }
        } finally {
            //释放资源
            returnResource(jedis);
        }

        return value;
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public static  Map<String, Object> getObjectMap(String key) {
        Map<String, Object> value = null;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(getBytesKey(key))) {
                value = Maps.newHashMap();
                Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
                for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
                    value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
                }
                logger.debug("getObjectMap {} = {}", key, value);
            }

        } finally {
            //释放资源
            returnResource(jedis);
        }

        return value;
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.hmset(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setMap {} = {}", key, value);

        } finally {
            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = getJedis();
        key = prefix+ key;


        try{

            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectMap {} = {}", key, value);

        } finally {
            //释放资源
            returnResource(jedis);
        }


        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static  String mapPut(String key, Map<String, String> value) {
        String result = null;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{

            result = jedis.hmset(key, value);
            logger.debug("mapPut {} = {}", key, value);

        } finally {

            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static  String mapObjectPut(String key, Map<String, Object> value) {
        String result = null;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{

            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
            logger.debug("mapObjectPut {} = {}", key, value);

        } finally {

            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public static  long mapRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{

            result = jedis.hdel(key, mapKey);
            logger.debug("mapRemove {}  {}", key, mapKey);

        } finally {

            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public static  long mapObjectRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = getJedis();


        try{
            key = prefix+ key;
            result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectRemove {}  {}", key, mapKey);

        } finally {
            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public static  boolean mapExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            result = jedis.hexists(key, mapKey);
            logger.debug("mapExists {}  {}", key, mapKey);

        } finally {
            //释放资源
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public static  boolean mapObjectExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectExists {}  {}", key, mapKey);
        } finally {

            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public static  long del(String key) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(key)) {
                result = jedis.del(key);
                logger.debug("del {}", key);
            } else {
                logger.debug("del {} not exists", key);
            }
        } finally {

            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public static  long delObject(String key) {
        long result = 0;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            if (jedis.exists(getBytesKey(key))) {
                result = jedis.del(getBytesKey(key));
                logger.debug("delObject {}", key);
            } else {
                logger.debug("delObject {} not exists", key);
            }
        } finally {

            //释放资源
            returnResource(jedis);
        }


        return result;
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public static  boolean exists(String key) {
        boolean result = false;
        Jedis jedis = getJedis();
        key = prefix+ key;

        try{
            result = jedis.exists(key);
            logger.debug("exists {}", key);
        } finally {

            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public static  boolean existsObject(String key) {
        boolean result = false;

        Jedis jedis = getJedis();
        key = prefix+ key;


        try{
            result = jedis.exists(getBytesKey(key));
            logger.debug("existsObject {}", key);
        } finally {

            //释放资源
            returnResource(jedis);
        }



        return result;
    }

    /**
     * 获取byte[]类型Key
     *
     * @param object
     * @return
     */
    public static  byte[] getBytesKey(Object object) {
        if (object instanceof String) {
            return StringUtils.getBytes((String) object);
        } else {
            return ObjectUtils.serialize(object);
        }
    }

    /**
     * Object转换byte[]类型
     *
     * @param object
     * @return
     */
    public static  byte[] toBytes(Object object) {
        return ObjectUtils.serialize(object);
    }

    /**
     * byte[]型转换Object
     *
     * @param bytes
     * @return
     */
    public static  Object toObject(byte[] bytes) {
        return ObjectUtils.unserialize(bytes);
    }


    /**
     * 对某个键的值自增
     * @author liboyi
     * @param key 键
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static  long setIncr(String key, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        key = prefix+ "requestCache:" + key;
        try {
            jedis = getJedis();
            result =jedis.incr(key);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            //logger.debug("set "+ key + " = " + result);
        } catch (Exception e) {
            //logger.warn("set "+ key + " = " + result);
        } finally {
            //释放资源
            returnResource(jedis);
        }
        return result;
    }


    /**
     * 获取Jedis实例
     * @return
     */
    private  static Jedis getJedis() {

        return jedisPool.getResource();
    }


    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    private static  void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedis.close();
        }
    }


}
