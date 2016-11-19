package com.github.yu000hong.spring.common.redis

import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisStringCommands
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.types.Expiration
import org.springframework.data.redis.serializer.RedisSerializer

import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

import static com.github.yu000hong.spring.common.util.JsonUtil.*

/**
 * Redis缓存处理
 * －只针对Redis普通的String类型
 * －只处理ValueOperations类型
 * －超时时间为-1时, 表示永久缓存永不超时
 */
class RedisValueUtil extends RedisUtil {
    private ValueOperations<String, String> valueOps

    public RedisValueUtil(RedisOperations<String, String> redisOps) {
        super(redisOps)
        this.valueOps = redisOps.opsForValue()
    }

    public RedisValueUtil(RedisOperations<String, String> redisOps, long expireMinutes) {
        super(redisOps, expireMinutes)
        this.valueOps = redisOps.opsForValue()
    }

    //region /*-------------- get --------------*/

    /**
     * 获取Redis中的数据，并转换成指定类型
     * @param key 键
     * @param clz 类型
     * @param timeout 超时时间
     * @param unit 时间单位
     * @param closure 闭包
     * @return
     */
    public <T> T get(String key, Class<T> clz, long timeout, TimeUnit unit, Closure<T> closure) {
        def value = valueOps.get(key)
        if (value == null) {
            def obj = closure.call()
            set(key, obj, timeout, unit)
            return obj
        } else {
            return toObject(value, clz)
        }
    }

    /**
     * 获取Redis中的数据，并转换成指定类型
     * @param key 键
     * @param type 特定类型
     * @param timeout 超时时间
     * @param unit 时间单位
     * @param closure 闭包
     * @return
     */
    public <T> T get(String key, Type type, long timeout, TimeUnit unit, Closure<T> closure) {
        def value = valueOps.get(key)
        if (value == null) {
            def obj = closure.call()
            set(key, obj, timeout, unit)
            return obj
        } else {
            return toObject(value, type)
        }
    }

    /**
     * 获取Redis中的数据，并转换成指定类型(缓存时间为默认时间)
     * @param key 键
     * @param clz 类型
     * @param closure 闭包
     * @return
     */
    public <T> T get(String key, Class<T> clz, Closure<T> closure) {
        return get(key, clz, expireMinutes, TimeUnit.MINUTES, closure)
    }

    /**
     * 获取Redis中的数据，并转换成指定类型(缓存时间为默认时间)
     * @param key 键
     * @param type 类型
     * @param closure 闭包
     * @return
     */
    public <T> T get(String key, Type type, Closure<T> closure) {
        return get(key, type, expireMinutes, TimeUnit.MINUTES, closure)
    }

    /**
     * 获取Redis中的数据，并转换成指定类型
     * @param key 键
     * @param clz 类型
     * @return
     */
    public <T> T get(String key, Class<T> clz) {
        def value = valueOps.get(key)
        return toObject(value, clz)
    }

    /**
     * 获取Redis中的数据，并转换成指定类型
     * @param key 键
     * @param type 类型
     * @return
     */
    public <T> T get(String key, Type type) {
        def value = valueOps.get(key)
        return toObject(value, type)
    }

    /**
     * 获取Redis中的数据，并转换成指定类型
     * @param key 键
     * @param clz 类型
     * @param defaultValue 默认值
     * @return
     */
    public <T> T get(String key, Class<T> clz, T defaultValue) {
        def value = valueOps.get(key)
        if (value == null) {
            return defaultValue
        } else {
            return toObject(value, clz)
        }
    }

    /**
     * 获取Redis中的数据，并转换成指定类型
     * @param key 键
     * @param type 类型
     * @param defaultValue 默认值
     * @return
     */
    public <T> T get(String key, Type type, T defaultValue) {
        def value = valueOps.get(key)
        if (value == null) {
            return defaultValue
        } else {
            return toObject(value, type)
        }
    }

    //endregion

    //region /* -------------- multiGet -------------- */

    /**
     * 从缓存里获取特定的数据类型(一一对应)
     * @param key 键
     * @param clz 特定类型
     * @return
     */
    public <T> List<T> multiGet(List<String> keys, Class<T> clz) {
        def values = valueOps.multiGet(keys)
        return toList(values, clz)
    }

    /**
     * 从缓存里获取特定的数据类型列表(一一对应)
     * @param ids ID列表
     * @param formatter Redis键的格式化字符串
     * @param clz 特定类型
     * @param key ID在对象中的字段名
     * @param timeout 超时时间
     * @param unit 时间单位
     * @param closure 以ID列表为参数的闭包
     * @return
     */
    public <T> List<T> multiGet(List ids, String formatter, Class<T> clz, String key, long timeout, TimeUnit unit, Closure<List<T>> closure) {
        def id2Obj = [:]
        def keys = ids.collect { id ->
            return String.format(formatter, id)
        }
        def list = multiGet(keys, clz)
        def unhitIds = []
        def i = 0
        list.each { item ->
            if (item == null) {
                unhitIds << ids[i]
            } else {
                id2Obj[ids[i]] = item
            }
            i++
        }
        if (unhitIds) {
            def key2Obj = [:]
            closure.call(unhitIds).each { item ->
                id2Obj[item[key]] = item
                key2Obj[String.format(formatter, item[key])] = item
            }
            multiSet(key2Obj, timeout, unit)
            (0..ids.size() - 1).each { int index ->
                if (list[index] == null) {
                    list[index] = id2Obj[ids[index]] as T
                }
            }
        }
        return list
    }

    /**
     * 从缓存里获取特定的数据类型列表(一一对应)
     * @param ids ID列表
     * @param formatter Redis键的格式化字符串
     * @param clz 特定类型
     * @param key ID在对象中的字段名
     * @param closure 以ID列表为参数的闭包
     * @return
     */
    public <T> List<T> multiGet(List ids, String formatter, Class<T> clz, String key, Closure<List<T>> closure) {
        return multiGet(ids, formatter, clz, key, expireMinutes, TimeUnit.MINUTES, closure)
    }

    //endregion

    //region /* -------------- list -------------- */

    /**
     * 从缓存里获取特定的数据类型映射
     * @param ids ID列表
     * @param formatter Redis键的格式化字符串
     * @param clz Redis键的格式化字符串
     * @param key ID在对象中的字段名
     * @param timeout 超时时间
     * @param unit 时间单位
     * @param closure 以ID列表为参数的闭包
     * @return
     */
    public <K, V> Map<K, V> list(List<K> ids, String formatter, Class<V> clz, String key, long timeout, TimeUnit unit, Closure<List<V>> closure) {
        def id2Obj = [:]
        def keys = ids.collect { id ->
            return String.format(formatter, id)
        }
        def list = multiGet(keys, clz)
        def unhitIds = []
        def i = 0
        list.each { item ->
            if (item == null) {
                unhitIds << ids[i]
            } else {
                id2Obj[ids[i]] = item
            }
            i++
        }
        if (unhitIds) {
            def key2Obj = [:]
            closure.call(unhitIds).each { item ->
                id2Obj[item[key]] = item
                key2Obj[String.format(formatter, item[key])] = item
            }
            multiSet(key2Obj, timeout, unit)
        }
        return id2Obj
    }

    /**
     * 从缓存里获取特定的数据类型映射
     * @param ids ID列表
     * @param formatter Redis键的格式化字符串
     * @param clz Redis键的格式化字符串
     * @param key ID在对象中的字段名
     * @param closure 以ID列表为参数的闭包
     * @return
     */
    public <K, V> Map<K, V> list(List<K> ids, String formatter, Class<V> clz, String key, Closure<List<V>> closure) {
        return list(ids, formatter, clz, key, expireMinutes, TimeUnit.MINUTES, closure)
    }

    //endregion

    //region /* -------------- set -------------- */

    /**
     * 添加object到缓存
     * @param key 键
     * @param obj 对象
     * @param timeout 过期时间量
     * @param unit 过期时间单位
     */
    public void set(String key, Object obj, long timeout, TimeUnit unit) {
        if (timeout == DO_NOT_EXPIRE) {
            valueOps.set(key, toJson(obj))
        } else {
            valueOps.set(key, toJson(obj), timeout, unit)
        }
    }

    /**
     * 添加object到缓存
     * @param key 键
     * @param obj 对象
     * @return
     */
    public void set(String key, Object obj) {
        set(key, obj, expireMinutes, TimeUnit.MINUTES)
    }

    //endregion

    //region /* -------------- setNx -------------- */

    /**
     * 如果指定key不存在时才设置相应的值
     * @param key 健
     * @param obj 对象
     * @return
     */
    public boolean setNx(String key, Object obj) {
        return setNx(key, obj, expireMinutes, TimeUnit.MINUTES)
    }

    /**
     * 如果指定key不存在时才设置相应的值
     * @param key 健
     * @param obj 对象
     * @param timeout 过期时间量
     * @param unit 过期时间单位
     * @return
     */
    public boolean setNx(String key, Object obj, long timeout, TimeUnit unit) {
        def success = valueOps.setIfAbsent(key, toJson(obj))
        if (success && timeout != DO_NOT_EXPIRE) {
            expire([key], timeout, unit)
        }
        return success
    }

    //endregion

    //region /* -------------- multiSet -------------- */

    /**
     * 批量设置
     * @param map 要设置的数据
     * @param timeout 超时时间
     * @param unit 时间单位
     */
    public void multiSet(Map<String, Object> map, long timeout, TimeUnit unit) {
        def keySerializer = redisOps.keySerializer as RedisSerializer<String>
        def valueSerializer = redisOps.valueSerializer as RedisSerializer<String>
        def expiration = null
        if (timeout != DO_NOT_EXPIRE) {
            expiration = Expiration.from(timeout, unit)
        }
        redisOps.executePipelined([doInRedis: { RedisConnection conn ->
            map.each { k, v ->
                def key = keySerializer.serialize(k)
                def value = valueSerializer.serialize(toJson(v))
                if (expiration) {
                    conn.set(key, value, expiration, RedisStringCommands.SetOption.UPSERT)
                } else {
                    conn.set(key, value)
                }
            }
            return null //this is a must!!
        }] as RedisCallback)
    }

    /**
     * 批量设置(默认超时时间)
     * @param map 键值对映射
     */
    public void multiSet(Map<String, Object> map) {
        multiSet(map, expireMinutes, TimeUnit.MINUTES)
    }

    //endregion

    //region /* -------------- increment & decrement -------------- */

    /**
     * 增加delta数值，可正可负
     * @param key 键
     * @param delta 增量
     * @return
     */
    public Long increment(String key, long delta) {
        return valueOps.increment(key, delta)
    }

    /**
     * 自增1
     * @param key 键
     * @return
     */
    public Long increment(String key) {
        return valueOps.increment(key, 1L)
    }

    /**
     * 自减1
     * @param key 键
     * @return
     */
    public Long decrement(String key) {
        return valueOps.increment(key, -1L)
    }

    //endregion

}
