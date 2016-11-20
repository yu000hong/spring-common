package com.github.yu000hong.spring.common.redis

import org.springframework.data.redis.core.RedisOperations

import static com.github.yu000hong.spring.common.redis.RedisUtil.DO_NOT_EXPIRE

/**
 * CounterUtil不是真正意义上的计数器
 * －CounterUtil计数器的值会过期消失
 * －如果缓存中存在计数器值，那么可以进行+1或-1
 * －如果缓存中不存在计数器值，那么do nothing
 * －因为不好解决计数器增减时被删除的并发问题, 所以计数器不能进行主动删除
 */
class CounterUtil {
    private static final long MIN_TTL = 2//2 seconds
    private RedisValueUtil redisValueUtil

    public CounterUtil(RedisOperations<String, String> redisOps) {
        this.redisValueUtil = new RedisValueUtil(redisOps)
    }

    public CounterUtil(RedisOperations<String, String> redisOps, long expireMinutes) {
        this.redisValueUtil = new RedisValueUtil(redisOps, expireMinutes)
    }

    /**
     * 获取计数器值
     * 如果Redis中没有，从closure中获取
     * 如果Redis中没有值，从closure中获取，并设置到Redis
     * @param keys 键列表
     * @param closure 闭包原型: Map<String,Long> function(Collection<String>) <br>
     *     closure的功能是根据提供的key列表获取对应的计数值，并以Map的形式返回
     * @return
     */
    public List<Long> multiGet(List<String> keys, Closure<Map<String, Long>> closure) {
        def values = redisValueUtil.multiGet(keys, Long.class)
        def unhitKeys = []
        values.eachWithIndex { counter, index ->
            if (counter == null) {
                unhitKeys << keys[index]
            }
        }
        if (unhitKeys) {
            def map = closure.call(unhitKeys)
            values.eachWithIndex { counter, index ->
                if (counter == null) {
                    values[index] = map[keys[index]]
                }
            }
            redisValueUtil.multiSet(map)
        }
        return values
    }

    /**
     * 获取计数器值
     * 如果Redis中没有，从closure中获取
     * 如果Redis中没有值，从closure中获取，并设置到Redis
     * @param key 键
     * @param closure 闭包
     * @return
     */
    public long get(String key, Closure<Long> closure) {
        def value = redisValueUtil.get(key, Long.class)
        if (value != null) {
            //如果计数器值存在, 直接返回
            return value
        }
        //根据Closure获取计数器值
        value = closure.call()
        if (redisValueUtil.setNx(key, value)) {
            //如果设置成功, 直接返回
            return value
        } else {
            //如果设置失败, 说明计数器值已被其他线程或进程设置
            return redisValueUtil.get(key, Long.class)
        }
    }

    /**
     * 计数器加1
     * 如果Redis中没有值，则do nothing
     * @param key 键
     */
    public Long incr(String key) {
        def ttl = redisValueUtil.ttl(key)
        if (ttl == DO_NOT_EXPIRE || ttl >= MIN_TTL) {
            return redisValueUtil.increment(key)
        } else {
            //value does not exist or value will be expired immediately
            //do nothing
            return null
        }
    }

    /**
     * 计数器减1
     * 如果Redis中没有值，则do nothing
     * @param key 键
     */
    public Long decr(String key) {
        def ttl = redisValueUtil.ttl(key)
        if (ttl == DO_NOT_EXPIRE || ttl >= MIN_TTL) {
            return redisValueUtil.decrement(key)
        } else {
            //value does not exist or value will be expired immediately
            //do nothing
            return null
        }
    }

}
