package com.github.yu000hong.spring.common.redis

import com.github.yu000hong.spring.redis.mock.RedisMockConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static com.github.yu000hong.spring.common.redis.TestRedisBase.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse

class TestCounterUtil {
    private RedisMockConnectionFactory connectionFactory
    private RedisTemplate<String, String> redisTemplate
    private CounterUtil counterUtil

    @BeforeMethod
    private void BeforeMethod() {
        connectionFactory = new RedisMockConnectionFactory()
        redisTemplate = new RedisTemplate<>()
        redisTemplate.setConnectionFactory(connectionFactory)
        redisTemplate.setEnableDefaultSerializer(true)
        redisTemplate.setDefaultSerializer(new StringRedisSerializer())
        redisTemplate.afterPropertiesSet()
        counterUtil = new CounterUtil(redisTemplate, EXPIRE_MINUTES)
    }

    @Test
    public void testGet() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        assertFalse(redis.exists(key))
        def counter = counterUtil.get(key) {
            return 200
        }
        assertEquals(counter, 200)
        assertExpiration(redis, key, EXPIRE_MINUTES * 60, DELTA_SECONDS)
    }

    @Test
    public void testIncr() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        redis.set(key, '30')
        assertEquals(counterUtil.incr(key), 31L)
        assertEquals(redis.get(key), '31')
        redis.del(key)
        assertEquals(counterUtil.incr(key), null)
        redis.set(key, '30', 'ex', '1')
        assertEquals(counterUtil.incr(key), null)
        redis.set(key, '30', 'ex', '3')
        assertEquals(counterUtil.incr(key), 31L)
    }

    @Test
    public void testDecr() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        redis.set(key, '30')
        assertEquals(counterUtil.decr(key), 29L)
        assertEquals(redis.get(key), '29')
        redis.del(key)
        assertEquals(counterUtil.decr(key), null)
        redis.set(key, '30', 'ex', '1')
        assertEquals(counterUtil.decr(key), null)
        redis.set(key, '30', 'ex', '3')
        assertEquals(counterUtil.decr(key), 29L)
    }

    @Test
    public void testMultiGet() {
        def redis = connectionFactory.getRedisMock()
        def keys = ['key1', 'key2']
        def list = counterUtil.multiGet(keys) { List<String> unhitKeys ->
            def map = [:]
            unhitKeys.each { key ->
                map[key] = Long.parseLong(key.substring(3))
            }
            return map
        }
        assertEquals(list.size(), 2)
        assertEquals(list[0], 1L)
        assertEquals(list[1], 2L)
        assertExpiration(redis, keys, EXPIRE_MINUTES * 60, DELTA_SECONDS)
        assertEquals(redis.get(keys[0]), '1')
        assertEquals(redis.get(keys[1]), '2')

        redis.del(keys as String[])
        redis.set(keys[0], '8', 'ex', '300')
        list = counterUtil.multiGet(keys) { List<String> unhitKeys ->
            def map = [:]
            unhitKeys.each { key ->
                map[key] = Long.parseLong(key.substring(3))
            }
            return map
        }
        assertEquals(list.size(), 2)
        assertEquals(list[0], 8L)
        assertEquals(list[1], 2L)
        assertExpiration(redis, keys[0], 300, DELTA_SECONDS)
        assertExpiration(redis, keys[1], EXPIRE_MINUTES * 60, DELTA_SECONDS)
        assertEquals(redis.get(keys[0]), '8')
        assertEquals(redis.get(keys[1]), '2')
    }

}
