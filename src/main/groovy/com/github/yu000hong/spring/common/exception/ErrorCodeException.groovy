package com.github.yu000hong.spring.common.exception

import com.github.yu000hong.spring.common.response.ErrorCode

class ErrorCodeException extends RuntimeException {
    ErrorCode errorCode

    public ErrorCodeException(ErrorCode errorCode) {
        super(errorCode.msg)
        this.errorCode = errorCode
    }

}
