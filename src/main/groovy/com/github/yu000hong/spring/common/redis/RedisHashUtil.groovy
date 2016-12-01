package com.github.yu000hong.spring.common.redis

import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisOperations

import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

import static com.github.yu000hong.spring.common.util.JsonUtil.*

/**
 * 默认是不会失效的
 */
class RedisHashUtil extends RedisUtil {
    private HashOperations<String, String, String> hashOps

    public RedisHashUtil(RedisOperations<String, String> redisOps) {
        super(redisOps)
        this.expireMinutes = DO_NOT_EXPIRE
        this.hashOps = redisOps.opsForHash()
    }

    public RedisHashUtil(RedisOperations<String, String> redisOps, long expireMinutes) {
        super(redisOps, expireMinutes)
        this.hashOps = redisOps.opsForHash()
    }

    /**
     * 从缓存里获取特定的数据类型
     *
     * @param key 键
     * @param hashKey Hash键
     * @param clz 特定类型
     * @return
     */
    public <T> T get(String key, String hashKey, Class<T> clz) {
        def value = hashOps.get(key, hashKey)
        return toObject(value, clz)
    }

    /**
     * 从缓存里获取特定的数据类型
     *
     * @param key 键
     * @param hashKey Hash键
     * @param clz 特定类型
     * @return
     */
    public <T> T get(String key, String hashKey, Type type) {
        def value = hashOps.get(key, hashKey)
        return toObject(value, type)
    }

    /**
     * 从缓存里获取特定的数据类型
     *
     * @param key 键
     * @param hashKey Hash键
     * @param clz 特定类型
     * @param defaultValue 默认值
     * @return
     */
    public <T> T get(String key, String hashKey, Class<T> clz, T defaultValue) {
        def value = get(key, hashKey, clz)
        if (value == null) {
            value = defaultValue
        }
        return value
    }

    /**
     * 从缓存里获取特定的数据类型
     *
     * @param key 键
     * @param hashKey Hash键
     * @param type 特定类型
     * @param defaultValue 默认值
     * @return
     */
    public <T> T get(String key, String hashKey, Type type, T defaultValue) {
        def value = get(key, hashKey, type) as T
        if (value == null) {
            value = defaultValue
        }
        return value
    }

    /* -------------- multiGet -------------- */

    /**
     * 从缓存里获取特定的数据类型
     *
     * @param key 键
     * @param hashKeys Hash键
     * @param clz 特定类型
     * @return
     */
    public <T> List<T> multiGet(String key, Collection<String> hashKeys, Class<T> clz) {
        def values = hashOps.multiGet(key, hashKeys)
        return toList(values, clz)
    }

    /* -------------- put -------------- */

    /**
     * 添加object到缓存
     *
     * @param key 键
     * @param hashKey Hash键
     * @param obj 对象
     */
    public void put(String key, String hashKey, Object obj) {
        def value = toJson(obj)
        hashOps.put(key, hashKey, value)
        setExpiration(key)
    }

    public boolean putNx(String key, String hashKey, Object obj) {
        def value = toJson(obj)
        def success = hashOps.putIfAbsent(key, hashKey, value)
        setExpiration(key)
        return success
    }

    /**
     * 添加object到缓存
     *
     * @param key 键
     * @param map 键值对
     */
    public void multiPut(String key, Map<String, ?> map) {
        def paraMap = [:]
        map.each { k, obj ->
            paraMap[k] = toJson(obj)
        }
        hashOps.putAll(key, paraMap)
        setExpiration(key)
    }

    /* -------------- values -------------- */

    /**
     * 获取所有Hash值集合
     *
     * @param key 键
     * @param clz 特定类型
     * @return
     */
    public <T> List<T> values(String key, Class<T> clz) {
        def values = hashOps.values(key)
        return toList(values, clz)
    }

    /* -------------- entries -------------- */

    /**
     * 获取所有Hash键值对集合
     *
     * @param key 键
     * @param clz 特定类型
     * @return
     */
    public <T> Map<String, T> entries(String key, Class<T> clz) {
        def map = hashOps.entries(key)
        return toMap(map, clz)
    }

    /* -------------- size -------------- */

    /**
     * 获取所有Hash键值对集合
     *
     * @param key 键
     * @param clz 特定类型
     * @return
     */
    public long size(String key) {
        return hashOps.size(key)
    }

    /* -------------- increment -------------- */

    /**
     * 增加delta数值，可正可负
     *
     * @param key 键
     * @param hashKey Hash键
     * @param delta 增量
     * @return
     */
    public long increment(String key, String hashKey, long delta) {
        return hashOps.increment(key, hashKey, delta)
    }

    /* -------------- remove -------------- */

    /**
     * 从缓存中删除
     *
     * @param key 键
     * @param hashKeys Hash键
     */
    public void remove(String key, Collection<String> hashKeys) {
        hashOps.delete(key, hashKeys as String[])
    }

    private void setExpiration(String key) {
        if (expireMinutes != DO_NOT_EXPIRE && ttl(key) == DO_NOT_EXPIRE) {
            expire([key], expireMinutes, TimeUnit.MINUTES)
        }
    }

}
