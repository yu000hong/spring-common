package com.github.yu000hong.spring.common.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull

@Test
@ContextConfiguration(classes = SpringConfig)
class TestDoNothingDebugUtil extends AbstractTestNGSpringContextTests {
    @Autowired
    private DebugUtil debugUtil

    @Test
    public void testDebugUtilWithoutEnv() {
        assertNotNull(debugUtil)
        assertNull(debugUtil.env)
    }

    @Test
    public void testNoException() {
        debugUtil.log('nothing will happen')
        debugUtil.execute {
            println('nothing will happen')
        }
    }

    @Configuration
    static class SpringConfig {

        @Bean
        public DebugUtil getDebugUtil() {
            return new DebugUtil()
        }

    }
}
