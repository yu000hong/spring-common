package com.github.yu000hong.spring.common.redis

import org.springframework.data.redis.core.RedisOperations

import java.beans.PropertyEditorSupport

class RedisValueUtilEditor extends PropertyEditorSupport {

    public void setValue(Object value) {
        if (value instanceof RedisOperations) {
            super.setValue(new RedisValueUtil((RedisOperations) value))
        } else {
            throw new IllegalArgumentException('Editor supports only conversion of type ' + RedisOperations.class)
        }
    }

}