package com.github.yu000hong.spring.common.util

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class TaskUtil {
    private static final int DEFAULT_AWAIT_SECOND = 20

    private final List<Closure> tasks = []

    public static TaskUtil newInstance() {
        return new TaskUtil()
    }

    public TaskUtil execute(Closure closure) {
        tasks << closure
        return this
    }

    /**
     * 顺序执行
     */
    public TaskUtil await() {
        if (!tasks) {
            return this//do nothing
        }
        tasks.each { task ->
            task.call()
        }
        tasks.clear()//避免重复执行
        return this
    }

    /**
     * 并行执行
     * @param executor 执行器
     * @param seconds 等待时间
     */
    public TaskUtil await(Executor executor, int seconds) {
        if (!tasks) {
            return this//do nothing
        }
        def countDownLatch = new CountDownLatch(tasks.size())
        tasks.each { task ->
            executor.execute(new Runnable() {
                @Override
                void run() {
                    try {
                        task.call()
                    } finally {
                        countDownLatch.countDown()
                    }
                }
            })
        }
        tasks.clear()//避免重复执行
        countDownLatch.await(seconds, TimeUnit.SECONDS)
        return this
    }

    /**
     * 并行执行, 默认等待时间为20秒
     * @param executor 执行器
     */
    public TaskUtil await(Executor executor) {
        return await(executor, DEFAULT_AWAIT_SECOND)
    }

}
