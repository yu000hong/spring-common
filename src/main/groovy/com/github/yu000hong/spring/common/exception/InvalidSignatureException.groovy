package com.github.yu000hong.spring.common.exception

class InvalidSignatureException extends RuntimeException {
    String expected
    String actual

    public InvalidSignatureException(String expected, String actual) {
        super("invalid signature: expected(${expected}), actual($actual)")
        this.expected = expected
        this.actual = actual
    }

    public InvalidSignatureException() {
        super("invalid signature: expected(UNKNOWN), actual(NULL)")
        expected = 'UNKNOWN'
        actual = 'NULL'
    }
}
