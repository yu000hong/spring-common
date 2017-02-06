package com.github.yu000hong.spring.common.response

import static CommonCode.CUSTOM_FAIL_MSG
import static CommonCode.CUSTOM_SUCCESS_MSG
import static CommonCode.ERROR
import static CommonCode.FAIL
import static CommonCode.SUCCESS
import static com.github.yu000hong.spring.common.util.JsonUtil.toJson

class ResponseWrapper {

    /**
     * 返回成功状态码
     * @return
     */
    public static String success() {
        return wrap(SUCCESS)
    }

    /**
     * 返回成功状态码，同时让客户端显示提示信息
     * @param tip 提示信息
     * @return
     */
    public static String successTip(String tip) {
        return toJson([code: CUSTOM_SUCCESS_MSG.code, msg: tip])
    }

    /**
     * 返回失败状态码
     * @return
     */
    public static String fail() {
        return wrap(FAIL)
    }

    /**
     * 返回失败状态码，同时让客户端显示提示信息
     * @param tip 提示信息
     * @return
     */
    public static String failTip(String tip) {
        return toJson([code: CUSTOM_FAIL_MSG.code, msg: tip])
    }

    /**
     * 返回异常状态码
     * @return
     */
    public static String error() {
        return wrap(ERROR)
    }

    /**
     * 返回成功状态码，同时返回data数据
     * @param dataMap data数据
     * @return
     */
    public static String wrap(Object data) {
        if (data instanceof IResponseCode) {
            def code = data as IResponseCode
            return toJson([code: code.code, msg: code.msg])
        } else {
            return toJson([code: SUCCESS.code, msg: SUCCESS.msg, data: data])
        }
    }

    /**
     * 返回状态码，同时返回data数据
     * @param code 状态码
     * @param data 数据
     * @return
     */
    public static String wrap(IResponseCode code, Object data) {
        return toJson([code: code.code, msg: code.msg, data: data])
    }

}
