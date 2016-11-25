package com.github.yu000hong.spring.common.util

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull


class TestStringUtil {

    @Test
    public void testTruncate() {
        def text = null
        def len = 3
        assertNull(StringUtil.truncate(text, len))
        text = ''
        assertEquals(StringUtil.truncate(text, len), '')
        text = 'ab'
        assertEquals(StringUtil.truncate(text, len), 'ab')
        text = 'abc'
        assertEquals(StringUtil.truncate(text, len), 'abc')
        text = 'abcd'
        assertEquals(StringUtil.truncate(text, len), 'abc')
        text = 'abcde'
        assertEquals(StringUtil.truncate(text, len), 'abc')
    }

}
