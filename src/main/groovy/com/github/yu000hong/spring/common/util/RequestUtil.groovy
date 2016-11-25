package com.github.yu000hong.spring.common.util

import org.apache.http.Header
import org.apache.http.client.fluent.Request

class RequestUtil {

    public static Header head(String url, String header) {
        try {
            return Request.Head(url).execute().returnResponse().getFirstHeader(header)
        } catch (e) {
            e.printStackTrace()
            return null
        }
    }

    public static String genUrl(String url, Map<String, String> params) {
        if (!params) {
            return url
        } else {
            def query = params.collect { k, v ->
                "$k=${URLEncoder.encode(v, 'UTF-8')}"
            }.join('&')
            return "${url}?${query}"
        }
    }

}
