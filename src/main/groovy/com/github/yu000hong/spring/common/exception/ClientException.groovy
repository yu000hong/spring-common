package com.github.yu000hong.spring.common.exception

/**
 * 调用接口时的异常类型
 */
class ClientException extends Exception {

    public ClientException() {
        super()
    }

    public ClientException(String message) {
        super(message)
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause)
    }

}
