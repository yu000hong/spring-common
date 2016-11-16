package com.github.yu000hong.spring.common.exception

class InvalidSessionIdException extends RuntimeException {
    String sessionId

    InvalidSessionIdException(String sessionId) {
        super("invalid session id: ${sessionId}")
        this.sessionId = sessionId
    }

}
