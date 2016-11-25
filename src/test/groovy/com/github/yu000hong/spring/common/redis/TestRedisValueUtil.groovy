package com.github.yu000hong.spring.common.redis

import com.github.yu000hong.spring.redis.mock.RedisMockConnectionFactory
import com.google.gson.reflect.TypeToken
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static com.github.yu000hong.spring.common.redis.RedisUtil.DO_NOT_EXPIRE
import static com.github.yu000hong.spring.common.redis.TestRedisBase.*
import static org.testng.Assert.*

class TestRedisValueUtil {
    private RedisMockConnectionFactory connectionFactory
    private RedisTemplate<String, String> redisTemplate
    private RedisValueUtil redisValueUtil

    @BeforeMethod
    private void beforeMethod() {
        connectionFactory = new RedisMockConnectionFactory()
        redisTemplate = new RedisTemplate<>()
        redisTemplate.setConnectionFactory(connectionFactory)
        redisTemplate.setEnableDefaultSerializer(true)
        redisTemplate.setDefaultSerializer(new StringRedisSerializer())
        redisTemplate.afterPropertiesSet()
        redisValueUtil = new RedisValueUtil(redisTemplate, EXPIRE_MINUTES)
    }

    @Test
    public void testGet() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        def value = '3.14'
        assertEquals(redisValueUtil.get(key, Double, 2.33D), 2.33D)
        assertEquals(redisValueUtil.get(key, new TypeToken<Double>() {}.type, 2.33D), 2.33D)
        redis.set(key, value)
        assertEquals(redisValueUtil.get(key, Double, 2.33D), 3.14D)
        assertEquals(redisValueUtil.get(key, new TypeToken<Double>() {}.type, 2.33D), 3.140D)
        assertEquals(redisValueUtil.get(key, Double), 3.14D)
        assertEquals(redisValueUtil.get(key, new TypeToken<Double>() {}.type), 3.140D)
        value = 'null'
        redis.set(key, value)
        assertEquals(redisValueUtil.get(key, Double, 2.33D), null)
        assertEquals(redisValueUtil.get(key, new TypeToken<Double>() {}.type, 2.33D), null)
        assertEquals(redisValueUtil.get(key, Double), null)
        assertEquals(redisValueUtil.get(key, new TypeToken<Double>() {}.type), null)
        redis.del(key)
        def closure = { return 3.334D }
        assertEquals(redisValueUtil.get(key, Double, closure), 3.334D)
        assertEquals(redisValueUtil.get(key, new TypeToken<Double>() {}.type, closure), 3.334D)
        assertExpiration(redis, key, EXPIRE_MINUTES * 60, DELTA_SECONDS)
        def seconds = 200
        redis.del(key)
        assertEquals(redisValueUtil.get(key, Double, seconds, TimeUnit.SECONDS, closure), 3.334D)
        assertExpiration(redis, key, seconds, DELTA_SECONDS)
    }

    @Test
    public void testMultiGet() {
        def redis = connectionFactory.getRedisMock()
        def keys = ['key1', 'key2']
        redis.set(keys[0], '3.44')
        def list = redisValueUtil.multiGet(keys, Double)
        assertEquals(list.size(), keys.size())
        assertEquals(list[0], 3.440D)
        assertNull(list[1])
    }

    @Test
    public void testMultiGetClosure() {
        def redis = connectionFactory.getRedisMock()
        def formatter = 'key%d'
        def ids = [1L, 2L]
        def keys = ids.collect { id ->
            return String.format(formatter, id)
        }
        def closure = { List<Long> unhitIds ->
            return unhitIds.collect { id ->
                return new TestObj(id: id, name: "name$id")
            }
        }
        def list = redisValueUtil.multiGet(ids, formatter, TestObj, 'id', closure)
        assertEquals(list.size(), 2)
        assertEquals(list[0].id, 1L)
        assertEquals(list[0].name, 'name1')
        assertEquals(list[1].id, 2L)
        assertEquals(list[1].name, 'name2')
        assertExpiration(redis, keys, EXPIRE_MINUTES * 60, DELTA_SECONDS)
        redis.del(keys as String[])
        list = redisValueUtil.multiGet(keys, TestObj)
        assertEquals(list.size(), 2)
        assertNull(list[0])
        assertNull(list[1])
        def seconds = 200
        redisValueUtil.multiGet(ids, formatter, TestObj, 'id', seconds, TimeUnit.SECONDS, closure)
        assertExpiration(redis, keys, seconds, DELTA_SECONDS)
    }

    @Test
    public void testList() {
        def redis = connectionFactory.getRedisMock()
        def formatter = 'key%d'
        def ids = [1L, 2L]
        def keys = ids.collect { id ->
            return String.format(formatter, id)
        }
        def closure = { List<Long> unhitIds ->
            return unhitIds.collect { id ->
                return new TestObj(id: id, name: "name$id")
            }
        }
        def map = redisValueUtil.list(ids, formatter, TestObj, 'id', closure)
        assertEquals(map.size(), 2)
        assertEquals(map[1L].id, 1L)
        assertEquals(map[1L].name, 'name1')
        assertEquals(map[2L].id, 2L)
        assertEquals(map[2L].name, 'name2')
        redis.del(keys as String[])
        def seconds = 200
        redisValueUtil.list(ids, formatter, TestObj, 'id', seconds, TimeUnit.SECONDS, closure)
        assertExpiration(redis, keys, seconds, DELTA_SECONDS)
    }

    @Test
    public void testSet() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        def value = false
        def seconds = 200
        assertFalse(redis.exists(key))
        redisValueUtil.set(key, value)
        assertExpiration(redis, key, EXPIRE_MINUTES * 60, DELTA_SECONDS)
        redisValueUtil.set(key, value, DO_NOT_EXPIRE, TimeUnit.MINUTES)
        assertDoNotExpire(redis, key)
        assertEquals(redis.get(key), 'false')
        redisValueUtil.set(key, value, seconds, TimeUnit.SECONDS)
        assertExpiration(redis, key, seconds, DELTA_SECONDS)
    }

    @Test
    public void testSetNx() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        def value = false
        def seconds = 200
        redis.set(key, String.valueOf(value))
        assertFalse(redisValueUtil.setNx(key, value))
        redis.del(key)
        assertTrue(redisValueUtil.setNx(key, value))
        assertExpiration(redis, key, EXPIRE_MINUTES * 60, DELTA_SECONDS)
        redis.del(key)
        assertTrue(redisValueUtil.setNx(key, value, seconds, TimeUnit.SECONDS))
        assertExpiration(redis, key, seconds, DELTA_SECONDS)
        redis.del(key)
        assertTrue(redisValueUtil.setNx(key, value, DO_NOT_EXPIRE, TimeUnit.SECONDS))
        assertDoNotExpire(redis, key)
    }

    @Test
    public void testMultiSet() {
        def redis = connectionFactory.getRedisMock()
        def keys = ['key1', 'key2', 'key3', 'key4']
        def map = [key1: 3,
                   key2: false,
                   key3: null,
                   key4: 'hello world']
        redisValueUtil.multiSet(map)
        assertEquals(redis.get('key1'), '3')
        assertEquals(redis.get('key2'), 'false')
        assertEquals(redis.get('key3'), 'null')
        assertEquals(redis.get('key4'), '"hello world"')
        assertExpiration(redis, keys, EXPIRE_MINUTES * 60, DELTA_SECONDS)
        redisValueUtil.multiSet(map, DO_NOT_EXPIRE, TimeUnit.DAYS)
        assertEquals(redis.get('key1'), '3')
        assertEquals(redis.get('key2'), 'false')
        assertEquals(redis.get('key3'), 'null')
        assertEquals(redis.get('key4'), '"hello world"')
        assertDoNotExpire(redis, keys)
        redisValueUtil.multiSet(map, 3, TimeUnit.MINUTES)
        assertExpiration(redis, keys, 3 * 60, DELTA_SECONDS)
    }

    @Test
    public void testIncrementAndDecrement() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        def value = '88'
        assertEquals(redisValueUtil.increment(key), 1L)
        redis.del(key)
        assertEquals(redisValueUtil.decrement(key), -1L)
        redis.set(key, value)
        assertEquals(redisValueUtil.increment(key, 2), 90L)
        redis.set(key, value)
        assertEquals(redisValueUtil.increment(key, -9), 79L)
        redis.del(key)
        assertEquals(redisValueUtil.increment(key, 2), 2L)
        assertDoNotExpire(redis, key)
    }

    private static class TestObj {
        long id
        String name
    }

}
