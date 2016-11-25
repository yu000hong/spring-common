package com.github.yu000hong.spring.common.redis

import com.github.yu000hong.spring.redis.mock.RedisMockConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static com.github.yu000hong.spring.common.redis.TestRedisBase.EXPIRE_MINUTES
import static org.testng.Assert.assertEquals

class TestRedisListUtil {

    private RedisMockConnectionFactory connectionFactory
    private RedisTemplate<String, String> redisTemplate
    private RedisListUtil redisListUtil

    @BeforeMethod
    private void beforeMethod() {
        connectionFactory = new RedisMockConnectionFactory()
        redisTemplate = new RedisTemplate<>()
        redisTemplate.setConnectionFactory(connectionFactory)
        redisTemplate.setEnableDefaultSerializer(true)
        redisTemplate.setDefaultSerializer(new StringRedisSerializer())
        redisTemplate.afterPropertiesSet()
        redisListUtil = new RedisListUtil(redisTemplate, EXPIRE_MINUTES)
    }

    @Test
    public void testSize() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        def value = '3.14'
        assertEquals(redisListUtil.size(key), 0)
        redis.lpush(key, value)
        assertEquals(redisListUtil.size(key), 1)
    }

    @Test
    public void testRange() {
        def redis = connectionFactory.getRedisMock()
        def key = 'testKey'
        assertEquals(redisListUtil.size(key), 0)
        redis.lpush(key, '1', '2', '3', '4')
        assertEquals(redisListUtil.size(key), 4)
        def list = redisListUtil.range(key, 0, 2, Long.class)
        assertEquals(list.size(), 3)
        assertEquals(list[0], 4L)
        assertEquals(list[1], 3L)
        assertEquals(list[2], 2L)
    }

}
