package com.github.yu000hong.spring.common.redis

import org.rarefiedredis.redis.RedisMock
import org.testng.Assert

import static com.github.yu000hong.spring.common.redis.RedisUtil.DO_NOT_EXPIRE

class TestRedisBase {
    public static final long EXPIRE_MINUTES = 10
    public static final long DELTA_SECONDS = 3

    public static void assertExpiration(RedisMock redis, String key, long seconds, long delta) {
        def ttl = redis.ttl(key)
        Assert.assertTrue(ttl <= seconds)
        Assert.assertTrue(ttl >= seconds - delta)
    }

    public static void assertExpiration(RedisMock redis, Collection<String> keys, long seconds, long delta) {
        keys.each { key ->
            assertExpiration(redis, key, seconds, delta)
        }
    }

    public static void assertDoNotExpire(RedisMock redis, String key) {
        def ttl = redis.ttl(key)
        Assert.assertEquals(ttl, DO_NOT_EXPIRE)
    }

    public static void assertDoNotExpire(RedisMock redis, Collection<String> keys) {
        keys.each { key ->
            assertDoNotExpire(redis, key)
        }
    }

}
