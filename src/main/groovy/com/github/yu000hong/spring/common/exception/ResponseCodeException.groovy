package com.github.yu000hong.spring.common.exception

import com.github.yu000hong.spring.common.response.IResponseCode

class ResponseCodeException extends RuntimeException {
    IResponseCode code

    public ResponseCodeException(IResponseCode code) {
        super(code.msg)
        this.code = code
    }

}
