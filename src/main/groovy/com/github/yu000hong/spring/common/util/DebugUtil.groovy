package com.github.yu000hong.spring.common.util

import com.github.yu000hong.spring.common.common.Env

/**
 * 用于调试的辅助工具
 */
class DebugUtil {
    private final Env env

    public DebugUtil(Env env) {
        this.env = env
    }

    /**
     * 打印调试日志
     * －在开发和测试环境下会直接打印到控制台
     * －在正式环境下，会直接忽略
     * @param message
     */
    void log(String message) {
        if (env != Env.PROD) {
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
        if (env != Env.PROD) {
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
        if (env != Env.PROD) {
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
        if (env != Env.PROD) {
            closure.call()
        }
    }

}
