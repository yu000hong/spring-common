package com.github.yu000hong.spring.common.redis

import org.springframework.dao.DataAccessException
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.serializer.RedisSerializer

import java.util.concurrent.TimeUnit

import static com.github.yu000hong.spring.common.util.DateUtil.toSeconds

abstract class RedisUtil {
    public static final long DEFAULT_EXPIRE_MINUTES = 5
    public static final long DO_NOT_EXPIRE = -1

    protected RedisOperations<String, String> redisOps
    protected int expireMinutes

    public RedisUtil(RedisOperations<String, String> redisOps) {
        this(redisOps, DEFAULT_EXPIRE_MINUTES)
    }

    public RedisUtil(RedisOperations<String, String> redisOps, long expireMinutes) {
        this.redisOps = redisOps
        this.expireMinutes = expireMinutes
    }

    /**
     * 设置缓存过期时间
     * @param keys 键列表
     * @param minutes 分钟
     */
    public void expire(Collection<String> keys, long time, TimeUnit unit) {
        if (!keys) return

        def keySerializer = redisOps.keySerializer as RedisSerializer<String>
        def seconds = toSeconds(time, unit)
        redisOps.executePipelined([doInRedis: { RedisConnection conn ->
            keys.each { k ->
                conn.expire(keySerializer.serialize(k), seconds)
            }
            return null //this is a must!!
        }] as RedisCallback)
    }

    /**
     * 设置缓存在指定时刻失效
     * @param keys 键列表
     * @param timestamp 毫秒
     */
    public void expireAt(Collection<String> keys, long timestamp) {
        if (!keys) return

        def keySerializer = redisOps.keySerializer as RedisSerializer<String>
        redisOps.executePipelined([doInRedis: { RedisConnection conn ->
            keys.each { k ->
                conn.pExpireAt(keySerializer.serialize(k), timestamp)
            }
            return null //this is a must!!
        }] as RedisCallback)
    }

    /**
     * 持久化Redis存储，TTL为-1
     * @param keys 键列表
     */
    public void persist(Collection<String> keys) {
        if (!keys) return

        def keySerializer = redisOps.keySerializer as RedisSerializer<String>
        redisOps.executePipelined([doInRedis: { RedisConnection conn ->
            keys.each { k ->
                conn.persist(keySerializer.serialize(k))
            }
            return null //this is a must!!
        }] as RedisCallback)
    }

    /**
     * 从缓存中删除
     * @param keys 键列表
     */
    public void delete(Collection<String> keys) {
        redisOps.delete(keys)
    }

    /**
     * 从缓存中删除
     * @param redis 缓存枚举
     * @param key 键列表
     */
    public boolean exists(String key) {
        def keySerializer = redisOps.keySerializer as RedisSerializer<String>
        return redisOps.execute(new RedisCallback<Boolean>() {
            @Override
            Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(keySerializer.serialize(key))
            }
        })
    }

}
