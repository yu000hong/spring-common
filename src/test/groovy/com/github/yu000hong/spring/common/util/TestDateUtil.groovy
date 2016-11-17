package com.github.yu000hong.spring.common.util

import org.testng.Assert
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static com.github.yu000hong.spring.common.util.DateUtil.toSeconds

class TestDateUtil {

    @Test
    public void testToSeconds() {
        def time = 1
        Assert.assertEquals(toSeconds(time, TimeUnit.DAYS), 24 * 60 * 60)
        Assert.assertEquals(toSeconds(time, TimeUnit.HOURS), 60 * 60)
        Assert.assertEquals(toSeconds(time, TimeUnit.MINUTES), 60)
        Assert.assertEquals(toSeconds(time, TimeUnit.SECONDS), 1)
        Assert.assertEquals(toSeconds(time, TimeUnit.MILLISECONDS), 0)
        Assert.assertEquals(toSeconds(30000, TimeUnit.MILLISECONDS), 30)
        Assert.assertEquals(toSeconds(3456, TimeUnit.MILLISECONDS), 3)
        Assert.assertEquals(toSeconds(0, TimeUnit.MILLISECONDS), 0)
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    public void testToSecondsExpectExceptions() {
        Assert.assertEquals(toSeconds(1000000, TimeUnit.MICROSECONDS), 1)
        Assert.assertEquals(toSeconds(1000000000, TimeUnit.NANOSECONDS), 1)
    }

}
