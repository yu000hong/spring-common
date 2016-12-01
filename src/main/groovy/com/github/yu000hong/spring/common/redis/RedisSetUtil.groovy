package com.github.yu000hong.spring.common.redis

import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.SetOperations

import static com.github.yu000hong.spring.common.util.JsonUtil.*

class RedisSetUtil extends RedisUtil {
    private SetOperations<String, String> setOps

    public RedisSetUtil(RedisOperations<String, String> redisOps) {
        super(redisOps)
        this.setOps = redisOps.opsForSet()
    }

    /* -------------- isMember -------------- */
    /**
     * 判断是否存在于集合中
     * @param key 键
     * @param obj 对象
     * @return
     */
    public boolean isMember(String key, Object obj) {
        def value = toJson(obj)
        return setOps.isMember(key, value)
    }

    /* -------------- add -------------- */
    /**
     * 添加对象到集合中
     * @param key 键
     * @param objs 对象集合
     * @return
     */
    public long add(String key, Collection<Object> objs) {
        if (!objs) {
            return 0L
        }

        def array = []
        objs.each { obj ->
            array << toJson(obj)
        }
        return setOps.add(key, array as String[])
    }

    /* -------------- remove -------------- */
    /**
     * 删除集合中的对象
     *
     * @param key 键
     * @param objs 对象集合
     * @return
     */
    public void remove(String key, Collection<Object> objs) {
        def array = []
        objs.each { obj ->
            array << toJson(obj)
        }
        setOps.remove(key, array as String[])
    }

    /* -------------- members -------------- */
    /**
     * 返回集合中的所有对象
     *
     * @param key 键
     * @param clz 对象类型
     * @return
     */
    public <T> Set<T> members(String key, Class<T> clz) {
        def values = setOps.members(key)
        return toSet(values, clz)
    }

    /* -------------- random -------------- */

    /**
     * 任意返回集合元素
     *
     * @param key 键
     * @param clz 对象类型
     * @return
     */
    public <T> T random(String key, Class<T> clz) {
        def value = setOps.randomMember(key)
        return toObject(value, clz)
    }

    /**
     * 任意返回集合元素
     *
     * @param key 键
     * @param clz 对象类型
     * @return
     */
    public <T> Set<T> random(String key, Class<T> clz, long count) {
        def values = setOps.randomMembers(key, count)
        return toSet(values.toSet(), clz)
    }

    /* -------------- size -------------- */
    /**
     * 返回集合大小
     *
     * @param key 键
     * @return
     */
    public long size(String key) {
        return setOps.size(key)
    }

}
