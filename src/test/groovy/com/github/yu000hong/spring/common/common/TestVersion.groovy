package com.github.yu000hong.spring.common.common

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

class TestVersion {

    @Test
    public void testFrom() {
        assertNull(Version.from(null))
        assertNull(Version.from(''))
        assertNotNull(Version.from('1'))
        assertNotNull(Version.from('1.1'))
        assertNotNull(Version.from('1.1.1'))
        assertTrue(Version.from('1.0.0') == Version.from('1'))
    }

    @Test(expectedExceptions = IllegalArgumentException)
    public void testFromException() {
        Version.from('1.1.1.1')
    }

    @Test
    public void testCompareTo() {
        assertTrue(Version.from('1.1') >= '1.1.0')
        assertTrue(Version.from('1.1') <= '1.1.0')
        assertTrue(Version.from('3.10.8') > '2.10.8')
        assertTrue(Version.from('3.10.8') > '2.10.9')
        assertTrue(Version.from('3.10.8') > '2.12.8')
        assertTrue(Version.from('3.10.8') > '2.12.9')
        assertTrue(Version.from('3.10.8') > '3.9.8')
        assertTrue(Version.from('3.10.8') > '3.9.28')
        assertTrue(Version.from('3.10.8') > '3.10.7')
    }

    @Test(expectedExceptions = ClassCastException)
    public void testInvalidCompareTo() {
        assertTrue('2.10.8' < Version.from('3.10.8'))
    }

    @Test
    public void testValidCompareTo() {
        assertTrue(Version.from('3.10.8') > '2.10.8')
    }

    @Test
    public void testEquals() {
        def version1 = Version.from('3.20.1')
        def version2 = new Version(major: 3, minor: 20, revision: 1)
        assertTrue(version1 == version2)

        version1 = Version.from('3.08.03')
        version2 = new Version(major: 3, minor: 8, revision: 3)
        assertTrue(version1 == version2)

        version1 = Version.from('2.1')
        version2 = new Version(major: 2, minor: 1)
        assertTrue(version1 == version2)
    }

    @Test
    public void testToString() {
        def version = Version.from('1.0')
        assertEquals(version.toString(), '1.0.0')
    }

    //region 抛异常和不抛异常的运行时间差距很大

    //testNothing - 2ms
    //testExceptionWhenFromInvalidVersion -232ms

    @Test(expectedExceptions = IllegalArgumentException)
    public void testExceptionWhenFromInvalidVersion() {
        Version.from('1.1.20.1')
    }

    @Test
    public void testNothing() {
        Version.from('1.1.20')
    }

    //endregion

}
