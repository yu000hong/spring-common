package com.github.yu000hong.spring.common.redis

import com.github.yu000hong.spring.redis.mock.RedisMockConnectionFactory
import com.google.gson.reflect.TypeToken
import org.rarefiedredis.redis.RedisMock
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.*

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

        redis.hset(key, hashKeys[0], '3.14')
        list = redisHashUtil.multiGet(key, hashKeys, Double)
        assertEquals(list.size(), hashKeys.size())
        assertEquals(list[0], 3.14D, 0.00001D)
        assertNull(list[1])

        redis.hset(key, hashKeys[1], '8')
        list = redisHashUtil.multiGet(key, hashKeys, Double)
        assertEquals(list.size(), hashKeys.size())
        assertEquals(list[0], 3.14D, 0.00001D)
        assertEquals(list[1], 8D, 0.00001D)
    }

    @Test
    public void testPut() {
        def key = 'key'
        def hashKey = 'hashKey'
        assertNull(redis.hget(key, hashKey))
        redisHashUtil.put(key, hashKey, 'hello world')
        assertEquals(redis.hget(key, hashKey), '"hello world"')
        redisHashUtil.put(key, hashKey, 3.14D)
        assertEquals(redis.hget(key, hashKey), '3.14')
    }

    @Test
    public void testPutNx() {
        def key = 'key'
        def hashKey = 'hashKey'
        assertNull(redis.hget(key, hashKey))
        redisHashUtil.putNx(key, hashKey, 'hello world')
        assertEquals(redis.hget(key, hashKey), '"hello world"')
        redisHashUtil.putNx(key, hashKey, '3.14')
        assertEquals(redis.hget(key, hashKey), '"hello world"')
    }

    @Test
    public void testMultiPut() {
        def key = 'key'
        def hashKeys = ['hashKey1', 'hashKey2', 'hashKey3']
        assertNull(redis.hget(key, hashKeys[0]))
        assertNull(redis.hget(key, hashKeys[1]))
        assertNull(redis.hget(key, hashKeys[2]))
        def map = [:]
        map[hashKeys[0]] = 1
        map[hashKeys[1]] = 9
        redisHashUtil.multiPut(key, map)
        assertEquals(redis.hget(key, hashKeys[0]), '1')
        assertEquals(redis.hget(key, hashKeys[1]), '9')
        assertNull(redis.hget(key, hashKeys[2]))
    }

    @Test
    public void testValues() {
        def key = 'key'
        assertNotNull(redisHashUtil.values(key, Long))
        assertEquals(redisHashUtil.values(key, Long).size(), 0)
        redis.hset(key, 'hashKey1', '1')
        redis.hset(key, 'hashKey2', '9')
        redis.hset(key, 'hashKey3', '8')
        assertEquals(redisHashUtil.values(key, Long).size(), 3)
        redis.hset(key, 'hashKey3', '10')
        assertEquals(redisHashUtil.values(key, Long).size(), 3)
        redis.hdel(key, 'hashKey1')
        assertEquals(redisHashUtil.values(key, Long).size(), 2)
    }

    @Test
    public void testEntries() {
        def key = 'key'
        assertNotNull(redisHashUtil.entries(key, Long))
        assertEquals(redisHashUtil.entries(key, Long).size(), 0)
        redis.hset(key, 'hashKey1', '1')
        redis.hset(key, 'hashKey2', '9')
        assertEquals(redisHashUtil.entries(key, Long).size(), 2)
        redis.hset(key, 'hashKey2', '8')
        assertEquals(redisHashUtil.entries(key, Long).size(), 2)
    }

    @Test
    public void testSize() {
        def key = 'key'
        assertEquals(redisHashUtil.size(key), 0)
        redis.hset(key, 'hashKey1', '1')
        redis.hset(key, 'hashKey2', '9')
        redis.hset(key, 'hashKey3', '8')
        assertEquals(redisHashUtil.size(key), 3)
        redis.hset(key, 'hashKey3', '10')
        assertEquals(redisHashUtil.size(key), 3)
        redis.hdel(key, 'hashKey1')
        assertEquals(redisHashUtil.size(key), 2)
    }

    @Test
    public void testIncrement() {
        def key = 'key'
        def hashKey = 'hashKey'
        assertNull(redis.hget(key, hashKey))
        redisHashUtil.increment(key, hashKey, 3)
        assertEquals(redis.hget(key, hashKey), '3')
        redisHashUtil.increment(key, hashKey, -4)
        assertEquals(redis.hget(key, hashKey), '-1')
    }

    @Test
    public void testRemove() {
        def key = 'key'
        def hashKeys = ['hashKey1', 'hashKey2', 'hashKey3']
        assertEquals(redis.hlen(key), 0)
        redisHashUtil.remove(key, hashKeys)
        assertEquals(redis.hlen(key), 0)
        redis.hset(key, hashKeys[0], '1')
        redis.hset(key, hashKeys[1], '2')
        assertEquals(redis.hlen(key), 2)
        redisHashUtil.remove(key, [hashKeys[0]])
        assertEquals(redis.hlen(key), 1)
        assertEquals(redis.hget(key, hashKeys[1]), '2')
        redisHashUtil.remove(key, hashKeys)
        assertEquals(redis.hlen(key), 0)
    }

}
