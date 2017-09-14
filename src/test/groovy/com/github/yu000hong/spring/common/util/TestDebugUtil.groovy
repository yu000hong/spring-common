package com.github.yu000hong.spring.common.util

import com.github.yu000hong.spring.common.common.Env
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull

@Test
@ContextConfiguration(classes = SpringConfig)
class TestDebugUtil extends AbstractTestNGSpringContextTests {
    @Autowired
    private DebugUtil debugUtil

    @Test
    public void testDebugUtilWithEnv() {
        assertNotNull(debugUtil)
    }

    @Configuration
    static class SpringConfig {

        @Bean
        public DebugUtil getDebugUtil() {
            return new DebugUtil(Env.DEV)
        }

    }
}
