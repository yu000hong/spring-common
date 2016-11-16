package com.github.yu000hong.spring.common.exception

class IllegalParameterException extends RuntimeException {
    private String parameter

    public IllegalParameterException(String parameter, String message) {
        super(message)
        this.parameter = parameter
    }

}
