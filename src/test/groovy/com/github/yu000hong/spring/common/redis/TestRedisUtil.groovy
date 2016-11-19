package com.github.yu000hong.spring.common.redis

import com.yu000hong.spring.redis.mock.RedisMockConnectionFactory
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testng.Assert
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

class TestRedisUtil {
    private RedisMockConnectionFactory connectionFactory
    private RedisTemplate<String, String> redisTemplate

    @BeforeTest
    private void beforeTest() {
        connectionFactory = new RedisMockConnectionFactory()
        redisTemplate = new RedisTemplate<>()
        redisTemplate.setConnectionFactory(connectionFactory)
        redisTemplate.setEnableDefaultSerializer(true)
        redisTemplate.setDefaultSerializer(new StringRedisSerializer())
        redisTemplate.afterPropertiesSet()
    }

    @Test
    public void testExpire() {
        def stub = new Stub(redisTemplate)
        def keys = ['testKey1', 'testKey2']
        def redis = connectionFactory.getRedisMock()
        redis.set(keys[0], '1')
        redis.set(keys[1], '2')
        Assert.assertTrue(stub.exists(keys[0]))
        Assert.assertTrue(stub.exists(keys[1]))
        Assert.assertEquals(redis.ttl(keys[0]), -1L)
        Assert.assertEquals(redis.ttl(keys[1]), -1L)
        def minutes = 5//缓存时间
        def delta = 3//时间误差在3秒内
        stub.expire(keys, minutes, TimeUnit.MINUTES)
        def ttl1 = redis.ttl(keys[0])
        def ttl2 = redis.ttl(keys[1])
        Assert.assertTrue(ttl1 <= minutes * 60)
        Assert.assertTrue(ttl1 >= minutes * 60 - delta)
        Assert.assertTrue(ttl2 <= minutes * 60)
        Assert.assertTrue(ttl2 >= minutes * 60 - delta)
    }

    @Test
    public void testExpireAt() {
        def stub = new Stub(redisTemplate)
        def keys = ['testKey1', 'testKey2']
        def redis = connectionFactory.getRedisMock()
        redis.set(keys[0], '1')
        redis.set(keys[1], '2')
        Assert.assertTrue(stub.exists(keys[0]))
        Assert.assertTrue(stub.exists(keys[1]))
        Assert.assertEquals(redis.ttl(keys[0]), -1L)
        Assert.assertEquals(redis.ttl(keys[1]), -1L)
        def mills = 300000
        def timestamp = System.currentTimeMillis() + mills
        def delta = 3000//时间误差在3秒内
        stub.expireAt(keys, timestamp)
        def pttl1 = redis.pttl(keys[0])
        def pttl2 = redis.pttl(keys[1])
        println(pttl1)
        Assert.assertTrue(pttl1 <= mills)
        Assert.assertTrue(pttl1 >= mills - delta)
        Assert.assertTrue(pttl2 <= mills)
        Assert.assertTrue(pttl2 >= mills - delta)
    }

    @Test
    public void testPersist() {
        def stub = new Stub(redisTemplate)
        def keys = ['testKey1', 'testKey2']
        def redis = connectionFactory.getRedisMock()
        def delta = 3//时间误差在3秒内
        def seconds = 300
        redis.set(keys[0], '1')
        redis.expire(keys[0], seconds)
        redis.set(keys[1], '1')
        redis.expire(keys[1], seconds)
        def ttl1 = redis.ttl(keys[0])
        def ttl2 = redis.ttl(keys[1])
        println(ttl1)
        Assert.assertTrue(ttl1 <= seconds)
        Assert.assertTrue(ttl1 >= seconds - delta)
        Assert.assertTrue(ttl2 <= seconds)
        Assert.assertTrue(ttl2 >= seconds - delta)
        stub.persist(keys)
        ttl1 = redis.ttl(keys[0])
        ttl2 = redis.ttl(keys[1])
        Assert.assertEquals(ttl1, -1L)
        Assert.assertEquals(ttl2, -1L)
    }

    @Test
    public void testDelete() {
        def stub = new Stub(redisTemplate)
        def keys = ['testKey1', 'testKey2']
        def redis = connectionFactory.getRedisMock()
        redis.set(keys[0], '1')
        redis.set(keys[1], '1')
        Assert.assertTrue(redis.exists(keys[0]))
        Assert.assertTrue(redis.exists(keys[1]))
        stub.delete(keys)
        Assert.assertFalse(redis.exists(keys[0]))
        Assert.assertFalse(redis.exists(keys[1]))
    }

    @Test
    public void testExists() {
        def stub = new Stub(redisTemplate)
        def key = 'testKey'
        def redis = connectionFactory.getRedisMock()
        Assert.assertFalse(stub.exists(key))
        redis.set(key, '1')
        Assert.assertTrue(stub.exists(key))
        redis.del(key)
        Assert.assertFalse(stub.exists(key))
    }


    private static class Stub extends RedisUtil {
        public Stub(RedisOperations<String, String> redisOps) {
            super(redisOps)
        }
    }
}
