package com.github.yu000hong.spring.common.response

import com.github.yu000hong.spring.common.util.JsonUtil


class ResponseWrapper {

    /**
     * 返回成功状态码，同时返回data数据
     * @param dataMap data数据
     * @return
     */
    public static String wrap(Object data) {
        if (data instanceof ErrorCode) {
            def code = data as ErrorCode
            return JsonUtil.toJson([code: code.code, msg: code.msg])
        } else {
            return JsonUtil.toJson([code: ErrorCode.SUCCESS.code, msg: ErrorCode.SUCCESS.msg, data: data])
        }
    }

    /**
     * 返回成功状态码
     * @return
     */
    public static String success() {
        return wrap(ErrorCode.SUCCESS)
    }

    /**
     * 返回成功状态码，同时让客户端显示提示信息
     * @param tip 提示信息
     * @return
     */
    public static String successTip(String tip) {
        return JsonUtil.toJson([code: ErrorCode.CUSTOM_SUCCESS_MSG.code, msg: tip])
    }

    /**
     * 返回失败状态码
     * @return
     */
    public static String fail() {
        return wrap(ErrorCode.FAIL)
    }

    /**
     * 返回失败状态码，同时让客户端显示提示信息
     * @param tip 提示信息
     * @return
     */
    public static String failTip(String tip) {
        return JsonUtil.toJson([code: ErrorCode.CUSTOM_FAIL_MSG.code, msg: tip])
    }

    /**
     * 返回异常状态码
     * @return
     */
    public static String error() {
        return wrap(ErrorCode.ERROR)
    }

    /**
     * 返回状态码，同时返回data数据
     * @param errorCode 状态码
     * @param data 数据
     * @return
     */
    public static String wrap(ErrorCode errorCode, Object data) {
        return JsonUtil.toJson([code: errorCode.code, msg: errorCode.msg, data: data])
    }

}
