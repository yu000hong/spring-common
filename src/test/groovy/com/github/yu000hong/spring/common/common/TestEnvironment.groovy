package com.github.yu000hong.spring.common.common

import com.github.yu000hong.spring.common.util.DateUtil
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.text.ParseException

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

class TestEnvironment {
    private static final String KEY_VERSION = 'version'
    private static final String KEY_URL = 'url'
    private static final String KEY_NAMESPACE = 'namespace'
    private static final String KEY_MAX_RETRY = 'max.retry'
    private static final String KEY_PUSH_ENABLE = 'push-enable'
    private static final String KEY_EXCHANGE_RATE = 'exchange_rate'
    private static final String KEY_TODAY = 'today'
    private static final String KEY_NOW = 'now'
    private static final String KEY_NOT_EXISTS = 'not-exists'

    private Environment env

    @BeforeClass
    public void beforeClass() {
        def properties = new Properties()
        properties.put(KEY_VERSION, '3.6')
        properties.put(KEY_URL, 'http://weibo.com')
        properties.put(KEY_NAMESPACE, 'com.github.yu000hong')
        properties.put(KEY_MAX_RETRY, '10')
        properties.put(KEY_PUSH_ENABLE, 'true')
        properties.put(KEY_EXCHANGE_RATE, '6.8599')
        properties.put(KEY_TODAY, '2017-02-06')
        properties.put(KEY_NOW, '2017-02-06 23:08:23')
        env = new Environment(type: Environment.Type.DEV, props: properties)
    }

    @Test
    public void testType() {
        assertTrue(env.isDev())
        assertFalse(env.isTest())
        assertFalse(env.isProd())
    }

    @Test
    public void testGetString() {
        assertEquals(env.getString(KEY_VERSION), '3.6')
        assertEquals(env.getString(KEY_NAMESPACE), 'com.github.yu000hong')
        assertNull(env.getString(KEY_NOT_EXISTS))
        assertEquals(env.getString(KEY_NOT_EXISTS, 'default'), 'default')
    }

    @Test
    public void testGetInt() {
        assertEquals(env.getInt(KEY_MAX_RETRY), 10)
        assertNull(env.getInt(KEY_NOT_EXISTS))
        assertEquals(env.getInt(KEY_NOT_EXISTS, 20), 20)
    }

    @Test(expectedExceptions = NumberFormatException)
    public void testGetIntException() {
        env.getInt(KEY_NAMESPACE)
    }

    @Test
    public void testGetLong() {
        assertEquals(env.getLong(KEY_MAX_RETRY), 10)
        assertNull(env.getLong(KEY_NOT_EXISTS))
        assertEquals(env.getLong(KEY_NOT_EXISTS, 20), 20)
    }

    @Test(expectedExceptions = NumberFormatException)
    public void testGetLongException() {
        env.getLong(KEY_URL)
    }

    @Test
    public void testGetBoolean() {
        assertTrue(env.getBoolean(KEY_PUSH_ENABLE))
        assertNull(env.getBoolean(KEY_NOT_EXISTS))
        assertFalse(env.getBoolean(KEY_NOT_EXISTS, false))
    }

    @Test(expectedExceptions = IllegalArgumentException)
    public void testGetBooleanException() {
        env.getBoolean(KEY_MAX_RETRY)
    }

    @Test
    public void testGetDouble() {
        assertEquals(env.getDouble(KEY_MAX_RETRY), 10, 0.00001)
        assertNull(env.getDouble(KEY_NOT_EXISTS))
        assertEquals(env.getDouble(KEY_NOT_EXISTS, 20.1), 20.1, 0.00001)
        assertEquals(env.getDouble(KEY_EXCHANGE_RATE), 6.8599, 0.00001)
    }

    @Test(expectedExceptions = NumberFormatException)
    public void testGetDoubleException() {
        env.getDouble(KEY_URL)
    }

    @Test
    public void testGetFloat() {
        assertEquals(env.getFloat(KEY_MAX_RETRY), 10F, 0.00001F)
        assertNull(env.getFloat(KEY_NOT_EXISTS))
        assertEquals(env.getFloat(KEY_NOT_EXISTS, 20.1F), 20.1F, 0.00001F)
        assertEquals(env.getFloat(KEY_EXCHANGE_RATE), 6.8599F, 0.00001F)
    }

    @Test(expectedExceptions = NumberFormatException)
    public void testGetFloatException() {
        env.getFloat(KEY_URL)
    }

    @Test
    public void testGetTimestamp() {
        def timestamp = DateUtil.parseTime('2017-02-06 23:08:23')
        assertEquals(env.getTimestamp(KEY_NOW), timestamp)
        assertNull(env.getTimestamp(KEY_NOT_EXISTS))
        assertEquals(env.getTimestamp(KEY_NOT_EXISTS, timestamp), timestamp)
    }

    @Test(expectedExceptions = ParseException)
    public void testGetTimestampException() {
        env.getTimestamp(KEY_NAMESPACE)
    }

    @Test
    public void testGetDate() {
        def date = DateUtil.parseDate('2017-02-06')
        assertEquals(env.getDate(KEY_TODAY), date)
        assertNull(env.getDate(KEY_NOT_EXISTS))
        assertEquals(env.getDate(KEY_NOT_EXISTS, date), date)
    }

    @Test(expectedExceptions = ParseException)
    public void testGetDateException() {
        env.getDate(KEY_EXCHANGE_RATE)
    }

}
