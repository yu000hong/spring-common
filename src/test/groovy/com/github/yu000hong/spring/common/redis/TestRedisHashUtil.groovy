package com.github.yu000hong.spring.common.redis

import com.github.yu000hong.spring.redis.mock.RedisMockConnectionFactory
import com.google.gson.reflect.TypeToken
import org.rarefiedredis.redis.RedisMock
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull

class TestRedisHashUtil {
    private RedisMockConnectionFactory connectionFactory
    private RedisTemplate<String, String> redisTemplate
    private RedisHashUtil redisHashUtil
    private RedisMock redis

    @BeforeMethod
    private void beforeMethod() {
        connectionFactory = new RedisMockConnectionFactory()
        redisTemplate = new RedisTemplate<>()
        redisTemplate.setConnectionFactory(connectionFactory)
        redisTemplate.setEnableDefaultSerializer(true)
        redisTemplate.setDefaultSerializer(new StringRedisSerializer())
        redisTemplate.afterPropertiesSet()
        redisHashUtil = new RedisHashUtil(redisTemplate)
        redis = connectionFactory.getRedisMock()
    }

    @Test
    public void testGet() {
        def key = 'key'
        def hashKey = 'hashKey'
        assertEquals(redisHashUtil.get(key, hashKey, Double), null)
        assertEquals(redisHashUtil.get(key, hashKey, new TypeToken<Double>() {}.type), null)
        assertEquals(redisHashUtil.get(key, hashKey, Double, 3.14), 3.14D, 0.001)
        assertEquals(redisHashUtil.get(key, hashKey, new TypeToken<Double>() {}.type, 3.14), 3.14D, 0.001)

        redis.hset(key, hashKey, 'null')
        assertEquals(redisHashUtil.get(key, hashKey, Double), null)
        assertEquals(redisHashUtil.get(key, hashKey, new TypeToken<Double>() {}.type), null)
        assertEquals(redisHashUtil.get(key, hashKey, Double, 3.14), 3.14D, 0.001)
        assertEquals(redisHashUtil.get(key, hashKey, new TypeToken<Double>() {}.type, 3.14), 3.14D, 0.001)

        redis.hset(key, hashKey, '3.13')
        assertEquals(redisHashUtil.get(key, hashKey, Double), 3.13D, 0.001)
        assertEquals(redisHashUtil.get(key, hashKey, new TypeToken<Double>() {}.type) as Double, 3.13D, 0.001)
        assertEquals(redisHashUtil.get(key, hashKey, Double, 3.14), 3.13D, 0.001)
        assertEquals(redisHashUtil.get(key, hashKey, new TypeToken<Double>() {}.type, 3.14), 3.13D, 0.001)
    }

    @Test
    public void testMultiGet() {
        def key = 'key'
        def hashKeys = ['hashKey1', 'hashKey2']

        def list = redisHashUtil.multiGet(key, hashKeys, Double)
        assertEquals(list.size(), hashKeys.size())
        assertNull(list[0])
        assertNull(list[1])

        redis.hset(key, hashKeys[0])
    }


}
