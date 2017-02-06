package com.github.yu000hong.spring.common.common

import com.github.yu000hong.spring.common.util.DateUtil
import groovy.transform.ToString

import java.sql.Timestamp

@ToString
class Environment {
    Type type //环境：开发、测试、正式
    Properties props

    /*--------------- 部署环境相关方法 ---------------*/

    /**
     * 是否是开发环境
     * @return
     */
    boolean isDev() {
        return type == Type.DEV
    }

    /**
     * 是否是测试环境
     * @return
     */
    boolean isTest() {
        return type == Type.TEST
    }

    /**
     * 是否是正式环境
     * @return
     */
    boolean isProd() {
        return type == Type.PROD
    }

    public static enum Type {
        DEV,//开发环境
        TEST,//测试环境
        PROD//正式环境
    }

    /*--------------- 一些获取属性，并解析成相应类型的方法 ---------------*/

    /**
     * 获取字符串属性
     * @param key 键
     * @return
     */
    String getString(String key) {
        return props.getProperty(key)
    }

    /**
     * 获取字符串属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    String getString(String key, String defaultValue) {
        return props.getProperty(key) ?: defaultValue
    }

    /**
     * 获取整型属性
     * @param key 键
     * @return
     */
    Integer getInt(String key) {
        def value = props.getProperty(key)
        return value == null ? null : value.toInteger()
    }

    /**
     * 获取整型属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    Integer getInt(String key, int defaultValue) {
        def value = props.getProperty(key)
        return value == null ? defaultValue : value.toInteger()
    }

    /**
     * 获取长整型属性
     * @param key 键
     * @return
     */
    Long getLong(String key) {
        def value = props.getProperty(key)
        return value == null ? null : value.toLong()
    }

    /**
     * 获取长整型属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    Long getLong(String key, long defaultValue) {
        def value = props.getProperty(key)
        return value == null ? defaultValue : value.toLong()
    }

    /**
     * 获取布尔型属性
     * @param key 键
     * @return
     */
    Boolean getBoolean(String key) {
        def value = props.getProperty(key)
        if (value == null) {
            return null
        } else {
            switch (value.toLowerCase()) {
                case 'true':
                    return true
                case 'false':
                    return false
                default:
                    throw new IllegalArgumentException()
            }
        }
    }

    /**
     * 获取布尔型属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    Boolean getBoolean(String key, boolean defaultValue) {
        def value = props.getProperty(key)
        if (value == null) {
            return defaultValue
        } else {
            switch (value.toLowerCase()) {
                case 'true':
                    return true
                case 'false':
                    return false
                default:
                    throw new IllegalArgumentException()
            }
        }
    }

    /**
     * 获取Double类型属性
     * @param key 键
     * @return
     */
    Double getDouble(String key) {
        def value = props.getProperty(key)
        return value == null ? null : value.toDouble()
    }

    /**
     * 获取Double类型属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    Double getDouble(String key, double defaultValue) {
        def value = props.getProperty(key)
        return value == null ? defaultValue : value.toDouble()
    }

    /**
     * 获取浮点型属性
     * @param key 键
     * @return
     */
    Float getFloat(String key) {
        def value = props.getProperty(key)
        return value == null ? null : value.toFloat()
    }

    /**
     * 获取浮点型属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    Float getFloat(String key, float defaultValue) {
        def value = props.getProperty(key)
        return value == null ? defaultValue : value.toFloat()
    }

    /**
     * 获取Timestamp类型属性
     * @param key 键
     * @return
     */
    Timestamp getTimestamp(String key) {
        def value = props.getProperty(key)
        return value == null ? null : DateUtil.parseTime(value)
    }

    /**
     * 获取Timestamp类型属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    Timestamp getTimestamp(String key, Timestamp defaultValue) {
        def value = props.getProperty(key)
        return value == null ? defaultValue : DateUtil.parseTime(value)
    }

    /**
     * 获取Timestamp类型属性
     * @param key 键
     * @return
     */
    java.sql.Date getDate(String key) {
        def value = props.getProperty(key)
        return value == null ? null : DateUtil.parseDate(value)
    }

    /**
     * 获取Timestamp类型属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    java.sql.Date getDate(String key, java.sql.Date defaultValue) {
        def value = props.getProperty(key)
        return value == null ? defaultValue : DateUtil.parseDate(value)
    }

}
