package com.github.yu000hong.spring.common.util

import com.github.yu000hong.spring.common.common.Environment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * 用于调试的辅助工具
 */
@Component
class DebugUtil {
    @Autowired(required = false)
    private Environment environment

    /**
     * 打印调试日志
     * －在开发和测试环境下会直接打印到控制台
     * －在正式环境下，会直接忽略
     * @param message
     */
    void log(String message) {
        if (environment && !environment.isProd()) {
            println(message)
        }
    }

    /**
     * 执行特定调试代码
     * －在开发和测试环境下才会执行相应代码
     * －在正式环境下，会直接忽略
     * @param closure
     */
    void execute(Closure closure) {
        if (environment && !environment.isProd()) {
            closure.call()
        }
    }
}
