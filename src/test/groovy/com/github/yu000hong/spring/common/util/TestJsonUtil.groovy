package com.github.yu000hong.spring.common.util

import com.google.gson.JsonSyntaxException
import org.testng.Assert
import org.testng.annotations.Test

import java.sql.Date
import java.sql.Timestamp

class TestJsonUtil {

    @Test
    public void testToJson() {
        def i = 3
        def d = 3.141500D
        def b = false
        def str = 'hello world'
        def now = System.currentTimeMillis()
        def time = new Timestamp(now)
        def date = new Date(now)
        def map = [name: 'yh']
        Assert.assertEquals(JsonUtil.toJson(i), '3')
        Assert.assertEquals(JsonUtil.toJson(d), '3.1415')
        Assert.assertEquals(JsonUtil.toJson(b), 'false')
        Assert.assertEquals(JsonUtil.toJson(str), '"hello world"')
        Assert.assertEquals(JsonUtil.toJson(time), String.valueOf(now))
        Assert.assertEquals(JsonUtil.toJson(date), String.valueOf(now))
        Assert.assertEquals(JsonUtil.toJson(map), '{"name":"yh"}')
        Assert.assertEquals(JsonUtil.toJson(null), 'null')
    }

    @Test
    public void testFromJson() {
        def i = 3
        def d = 3.141500D
        def b = false
        def str = 'hello world'
        def now = System.currentTimeMillis()
        def time = new Timestamp(now)
        def date = new Date(now)
        def map = [name: 'yh']
        Assert.assertEquals(JsonUtil.fromJson('3', Integer), i)
        Assert.assertEquals(JsonUtil.fromJson('3.141500', Double), d)
        Assert.assertEquals(JsonUtil.fromJson('false', Boolean), b)
        Assert.assertEquals(JsonUtil.fromJson('"hello world"', String), str)
        Assert.assertEquals(JsonUtil.fromJson(String.valueOf(now), Timestamp), time)
        Assert.assertEquals(JsonUtil.fromJson(String.valueOf(now), Date), date)
        Assert.assertEquals(JsonUtil.fromJson('{"name":"yh"}', Map), map)
        Assert.assertEquals(JsonUtil.fromJson('null', String), null)
        Assert.assertEquals(JsonUtil.fromJson('"null"', String), 'null')
    }

    @Test(expectedExceptions = JsonSyntaxException)
    public void testFromJsonThrowException() {
        Assert.assertEquals(JsonUtil.fromJson('null', String), null)
        Assert.assertEquals(JsonUtil.fromJson('"null"', String), 'null')
        Assert.assertEquals(JsonUtil.fromJson('hello', String), 'hello')
        Assert.assertEquals(JsonUtil.fromJson('"hello"', String), 'hello')
        Assert.assertEquals(JsonUtil.fromJson('"hello world"', String), 'hello world')
        //throw exception
        JsonUtil.fromJson('hello world', String)
    }

    @Test
    public void testToObject() {
        def i = 3
        def d = 3.141500D
        def b = false
        def now = System.currentTimeMillis()
        def time = new Timestamp(now)
        def date = new Date(now)
        Assert.assertEquals(JsonUtil.toObject('3', Integer), i)
        Assert.assertTrue(JsonUtil.toObject('3.141500', Double) == d)
        Assert.assertEquals(JsonUtil.toObject('3.141500', Double), d)
        Assert.assertEquals(JsonUtil.toObject('false', Boolean), b)
        Assert.assertEquals(JsonUtil.toObject(String.valueOf(now), Timestamp), time)
        Assert.assertEquals(JsonUtil.toObject(String.valueOf(now), Date), date)
        Assert.assertEquals(JsonUtil.toObject('null', Object), null)
        Assert.assertEquals(JsonUtil.toObject(null, Object), null)
    }

    @Test
    public void testToList() {
        def list1 = null
        def list2 = []
        def list3 = ['a', 'b', 'c']
        def list4 = ['1', '2', '3']
        def list5 = ['{"k1":1,"k2":2,"k3":3}']
        Assert.assertEquals(JsonUtil.toList(list1, Object), null)
        Assert.assertEquals(JsonUtil.toList(list2, Object), [])
        Assert.assertEquals(JsonUtil.toList(list3, String)[2], 'c')
        Assert.assertEquals(JsonUtil.toList(list4, Long)[1], 2)
        Assert.assertEquals(JsonUtil.toList(list5, Map).size(), 1)
        Assert.assertTrue(JsonUtil.toList(list5, Map)[0]['k1'] == 1)
    }

    @Test
    public void testToSet() {
        def set1 = null
        def set2 = [] as Set
        def set3 = ['a', 'b', 'c'] as Set
        def set4 = ['1', '2', '3'] as Set
        def set5 = ['{"k1":1,"k2":2,"k3":3}'] as Set
        Assert.assertEquals(JsonUtil.toSet(set1, Object), null)
        Assert.assertTrue(JsonUtil.toSet(set2, Object) instanceof Set)
        Assert.assertTrue('c' in JsonUtil.toSet(set3, String))
        Assert.assertTrue(2L in JsonUtil.toSet(set4, Long))
        Assert.assertTrue(JsonUtil.toSet(set5, Map) instanceof Set)
    }

    @Test
    public void testToMap() {
        def map1 = null
        def map2 = [:]
        def map3 = ['k1': '1', 'k2': '2', 'k3': '3']
        Assert.assertEquals(JsonUtil.toMap(map1, Object), null)
        Assert.assertTrue(JsonUtil.toMap(map2, Object) instanceof Map)
        Assert.assertEquals(JsonUtil.toMap(map2, Object).size(), 0)
        def converted = JsonUtil.toMap(map3, Long.class)
        Assert.assertEquals(converted.size(), 3)
        Assert.assertEquals(converted.get('k2'), 2L)
    }

}
