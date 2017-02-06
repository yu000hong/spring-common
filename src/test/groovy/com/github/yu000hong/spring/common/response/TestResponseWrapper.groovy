package com.github.yu000hong.spring.common.response

import com.github.yu000hong.spring.common.util.JsonUtil
import org.testng.annotations.Test

import static com.github.yu000hong.spring.common.response.CommonCode.CUSTOM_FAIL_MSG
import static com.github.yu000hong.spring.common.response.CommonCode.CUSTOM_SUCCESS_MSG
import static com.github.yu000hong.spring.common.response.CommonCode.ERROR
import static com.github.yu000hong.spring.common.response.CommonCode.FAIL
import static com.github.yu000hong.spring.common.response.CommonCode.SUCCESS
import static com.github.yu000hong.spring.common.response.ResponseWrapper.error
import static com.github.yu000hong.spring.common.response.ResponseWrapper.fail
import static com.github.yu000hong.spring.common.response.ResponseWrapper.failTip
import static com.github.yu000hong.spring.common.response.ResponseWrapper.success
import static com.github.yu000hong.spring.common.response.ResponseWrapper.successTip
import static com.github.yu000hong.spring.common.response.ResponseWrapper.wrap
import static com.github.yu000hong.spring.common.response.TestResponseWrapper.CustomCode.SAY_HELLO
import static com.github.yu000hong.spring.common.util.JsonUtil.getInt
import static com.github.yu000hong.spring.common.util.JsonUtil.getMap
import static com.github.yu000hong.spring.common.util.JsonUtil.getString
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull

class TestResponseWrapper {

    @Test
    public void testSuccess() {
        def response = success()
        def map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), SUCCESS.code)
        assertEquals(getString(map, 'msg'), SUCCESS.msg)
    }

    @Test
    public void testFail() {
        def response = fail()
        def map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), FAIL.code)
        assertEquals(getString(map, 'msg'), FAIL.msg)
    }

    @Test
    public void testError() {
        def response = error()
        def map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), ERROR.code)
        assertEquals(getString(map, 'msg'), ERROR.msg)
    }

    @Test
    public void testSuccessTip() {
        def response = successTip('哎哟, 不错哦')
        def map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), CUSTOM_SUCCESS_MSG.code)
        assertEquals(getString(map, 'msg'), '哎哟, 不错哦')
    }

    @Test
    public void testFailTip() {
        def response = failTip('404: Not Found')
        def map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), CUSTOM_FAIL_MSG.code)
        assertEquals(getString(map, 'msg'), '404: Not Found')
    }

    @Test
    public void testWrapCode() {
        def response = wrap(SAY_HELLO)
        def map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), SAY_HELLO.code)
        assertEquals(getString(map, 'msg'), SAY_HELLO.msg)
    }

    @Test
    public void testWrapData() {
        def response = wrap(null)
        def map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), SUCCESS.code)
        assertEquals(getString(map, 'msg'), SUCCESS.msg)
        assertNull(getMap(map, 'data'))

        def data = [content: 'this is a test']
        response = wrap(data)
        map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), SUCCESS.code)
        assertEquals(getString(map, 'msg'), SUCCESS.msg)
        def dataMap = getMap(map, 'data')
        assertNotNull(dataMap)
        assertEquals(getString(dataMap, 'content'), 'this is a test')
    }

    @Test
    public void testWrapCodeAndData() {
        def code = SAY_HELLO
        def data = [content: 'this is a test']
        def response = wrap(code, data)
        def map = JsonUtil.fromJson(response, Map)
        assertEquals(getInt(map, 'code'), SAY_HELLO.code)
        assertEquals(getString(map, 'msg'), SAY_HELLO.msg)
        def dataMap = getMap(map, 'data')
        assertNotNull(dataMap)
        assertEquals(getString(dataMap, 'content'), 'this is a test')
    }

    private static enum CustomCode implements IResponseCode {
        SAY_HELLO(10001, 'hello world~')

        final int code
        final String msg

        private CustomCode(int code, String msg) {
            this.code = code
            this.msg = msg
        }

    }

}
