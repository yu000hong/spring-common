package com.github.yu000hong.spring.common.common

import org.testng.Assert
import org.testng.annotations.Test

class TestVersion {

    @Test
    public void testFrom() {
        Assert.assertNull(Version.from(null))
        Assert.assertNull(Version.from(''))
        Assert.assertTrue(Version.from('1.0.0') == Version.from('1'))
        Assert.assertTrue(Version.from('1.1') >= '1.1.0')
        Assert.assertTrue(Version.from('1.1') <= '1.1.0')
        Assert.assertTrue(Version.from('1.10.1') > '1.9.0')
    }

    @Test(expectedExceptions = IllegalArgumentException)
    public void testExceptionWhenFromInvalidVersion() {
        Version.from('1.1.20.1')
    }

}
