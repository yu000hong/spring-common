package com.github.yu000hong.spring.common.util

class StringUtil {

    /**
     * 截断字符串,最大长度len
     * @param text 字符串
     * @param len 最大长度
     * @return
     */
    public static String truncate(String text, int len) {
        if (text == null || text.length() <= len) {
            return text
        }
        return text.substring(0, len)
    }

}
