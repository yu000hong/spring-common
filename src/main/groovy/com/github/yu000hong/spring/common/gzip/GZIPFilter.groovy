package com.github.yu000hong.spring.common.gzip

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

/**
 * 图片格式压缩过后基本还比原图片稍大点，所以图片格式不进行压缩处理
 * @author OpCAT
 * @07-May-2013 com.opcat.gzip
 */
class GZIPFilter implements Filter {

    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            def httpServletRequest = (HttpServletRequest) request
            def httpServletResponse = (HttpServletResponse) response
            def encoding = httpServletRequest.getHeader('accept-encoding')
            def path = httpServletRequest.requestURI
            //如果请求的是js或css文件，直接从文件系统读取
            if (encoding && encoding.indexOf('gzip') != -1
                    && (path.endsWith('.js') || path.endsWith('.css'))) {
                final HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(httpServletRequest) {
                    @Override
                    public StringBuffer getRequestURL() {
                        return new StringBuffer(path + '.gz')
                    }
                }
                chain.doFilter(wrapped, response)
                return
            }
            //如果不是图片文件，则进行压缩操作
            if (encoding && encoding.indexOf('gzip') != -1 //接受gzip压缩格式
                    && !path.endsWith('.png')//不是png图片格式
                    && !path.endsWith('.jpg')//不是jpg图片格式
                    && !path.endsWith('.gif')/*不是gif图片格式*/) {
                def wrapper = new GZIPResponseWrapper(httpServletResponse)
                chain.doFilter(request, wrapper)
                wrapper.finishResponse()
                return
            }
        }
        chain.doFilter(request, response)
    }

    void init(FilterConfig filterConfig) {
    }

    void destroy() {
    }

}