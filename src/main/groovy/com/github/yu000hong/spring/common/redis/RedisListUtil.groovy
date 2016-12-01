package com.github.yu000hong.spring.common.redis

import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisOperations

import static com.github.yu000hong.spring.common.util.JsonUtil.*

class RedisListUtil extends RedisUtil {
    private ListOperations<String, String> listOps

    public RedisListUtil(RedisOperations<String, String> redisOps) {
        super(redisOps)
        this.listOps = redisOps.opsForList()
    }

    public RedisListUtil(RedisOperations<String, String> redisOps, long expireMinutes) {
        super(redisOps, expireMinutes)
        this.listOps = redisOps.opsForList()
    }

    /**
     * 返回集合大小
     *
     * @param key 键
     * @return
     */
    public long size(String key) {
        return listOps.size(key)
    }

    /**
     * 返回集合子列表
     *
     * @param key 键
     * @param start 开始位移
     * @param end 结束位移
     * @param clz 对象类型
     * @return
     */
    public <T> List<T> range(String key, long start, long end, Class<T> clz) {
        def values = listOps.range(key, start, end)
        return toList(values, clz)
    }

    /**
     * left push
     *
     * @param key 键
     * @param objs 对象集合
     * @return
     */
    public void leftPush(String key, List<Object> objs) {
        def values = []
        objs.each { obj ->
            values << toJson(obj)
        }
        listOps.leftPushAll(key, values as String[])
    }

    /**
     * right push
     *
     * @param key 键
     * @param objs 对象集合
     * @return
     */
    public void rightPush(String key, List<Object> objects) {
        def values = []
        objects.each { obj ->
            values << toJson(obj)
        }
        if (values.size() == 0) {
            return
        }
        listOps.rightPushAll(key, values as String[])
    }

    /**
     * left pop
     *
     * @param key 键
     * @return
     */
    public <T> T leftPop(String key, Class<T> clz) {
        def value = listOps.leftPop(key)
        return toObject(value, clz)
    }

    /**
     * right pop
     *
     * @param key 键
     * @return
     */
    public <T> T rightPop(String key, Class<T> clz) {
        def value = listOps.rightPop(key)
        return toObject(value, clz)
    }

}
