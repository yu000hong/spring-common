package com.github.yu000hong.spring.common.util

import com.github.yu000hong.spring.common.common.Environment
import org.springframework.beans.factory.annotation.Autowired

/**
 * 用于调试的辅助工具
 */
class DebugUtil {
    @Autowired(required = false)
    private Environment env

    /**
     * 打印调试日志
     * －在开发和测试环境下会直接打印到控制台
     * －在正式环境下，会直接忽略
     * @param message
     */
    void log(String message) {
        if (env && !env.isProd()) {
            println(message)
        }
    }

    /**
     * 打印调试日志
     * －在开发和测试环境下会直接打印到控制台
     * －在正式环境下，会直接忽略
     * @param throwable
     */
    void error(Throwable throwable) {
        if (env && !env.isProd()) {
            throwable.printStackTrace()
        }
    }

    /**
     * 打印调试日志
     * －在开发和测试环境下会直接打印到控制台
     * －在正式环境下，会直接忽略
     * @param throwable
     * @param message
     */
    void error(Throwable throwable, String message) {
        if (env && !env.isProd()) {
            println(message)
            throwable.printStackTrace()
        }
    }

    /**
     * 执行特定调试代码
     * －在开发和测试环境下才会执行相应代码
     * －在正式环境下，会直接忽略
     * @param closure
     */
    void execute(Closure closure) {
        if (env && !env.isProd()) {
            closure.call()
        }
    }

}
