package com.github.yu000hong.spring.common.util

import org.testng.annotations.Test

import java.sql.Date
import java.sql.Timestamp
import java.text.ParseException
import java.util.concurrent.TimeUnit

import static com.github.yu000hong.spring.common.util.DateUtil.toSeconds
import static org.testng.Assert.assertEquals

class TestDateUtil {

    @Test
    public void testFormatDate() {
        def date = new Date(116, 11, 23)
        assertEquals(DateUtil.format(date), '2016-12-23')
    }

    @Test
    public void testFormatTime() {
        def time = new Timestamp(117, 2, 15, 11, 24, 0, 0)
        assertEquals(DateUtil.format(time), '2017-03-15 11:24:00')
    }

    @Test
    public void testParseDate() {
        def date = new Date(116, 11, 23)
        assertEquals(DateUtil.parseDate('2016-12-23'), date)
    }

    @Test(expectedExceptions = ParseException)
    public void testParseDateException() {
        DateUtil.parseDate('2016 12 23')
    }

    @Test
    public void testParseTime() {
        def time = new Timestamp(117, 2, 15, 11, 24, 0, 0)
        assertEquals(DateUtil.parseTime('2017-03-15 11:24:00'), time)
    }

    @Test(expectedExceptions = ParseException)
    public void testParseTimeException() {
        DateUtil.parseTime('2016-12-23')
    }

    @Test
    public void testToSeconds() {
        def time = 1
        assertEquals(toSeconds(time, TimeUnit.DAYS), 24 * 60 * 60)
        assertEquals(toSeconds(time, TimeUnit.HOURS), 60 * 60)
        assertEquals(toSeconds(time, TimeUnit.MINUTES), 60)
        assertEquals(toSeconds(time, TimeUnit.SECONDS), 1)
        assertEquals(toSeconds(time, TimeUnit.MILLISECONDS), 0)
        assertEquals(toSeconds(30000, TimeUnit.MILLISECONDS), 30)
        assertEquals(toSeconds(3456, TimeUnit.MILLISECONDS), 3)
        assertEquals(toSeconds(0, TimeUnit.MILLISECONDS), 0)
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    public void testToSecondsExpectExceptions() {
        assertEquals(toSeconds(1000000, TimeUnit.MICROSECONDS), 1)
        assertEquals(toSeconds(1000000000, TimeUnit.NANOSECONDS), 1)
    }

}
