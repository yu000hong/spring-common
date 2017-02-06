package com.github.yu000hong.spring.common.response


enum CommonCode implements IResponseCode {
    ERROR(-1, 'error'),
    SUCCESS(0, 'success'),
    FAIL(1, 'fail'),
    CUSTOM_SUCCESS_MSG(2, 'custom success message'),
    CUSTOM_FAIL_MSG(3, 'custom fail message'),
    INVALID_SIGNATURE(4, 'invalid signature'),
    INVALID_SESSION_ID(5, 'invalid session id'),
    INVALID_NONCE_STR(6, 'invalid nonce string'),
    INVALID_TIMESTAMP(7, 'invalid timestamp'),
    INVALID_REQUEST(8, 'invalid request'),
    OPERATION_FREQUENTLY(9, 'operation frequently'),
    OPERATION_DUPLICATED(10, 'operation duplicated'),
    NO_DATA(12, 'no data'),
    NO_QUOTA(13, 'no quota'),
    NO_PERMISSION(14, 'no permission')

    final int code
    final String msg

    private CommonCode(int code, String msg) {
        this.code = code
        this.msg = msg
    }

}
