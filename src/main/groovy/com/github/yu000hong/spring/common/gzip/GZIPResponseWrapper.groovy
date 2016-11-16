package com.github.yu000hong.spring.common.gzip

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

/**
 *
 * @author OpCAT
 * @07-May-2013 com.opcat.gzip
 */
class GZIPResponseWrapper extends HttpServletResponseWrapper {
    protected HttpServletResponse origResponse
    protected ServletOutputStream stream
    protected PrintWriter writer

    GZIPResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse)
        stream = null
        writer = null
        origResponse = httpServletResponse
    }

    ServletOutputStream createOutputStream() throws IOException {
        return new GZIPResponseStream(origResponse)
    }

    void finishResponse() {
        try {
            if (writer != null)
                writer.close()
            else if (stream != null)
                stream.close()
        } catch (IOException ignored) {
        }
    }

    void flushBuffer() throws IOException {
        stream.flush()
    }

    ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException('getWriter() has already been called!')
        }
        if (stream == null) {
            stream = createOutputStream()
        }
        return stream
    }

    PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer
        }
        if (stream != null) {
            throw new IllegalStateException('getOutputStream() has already been called!')
        } else {
            stream = createOutputStream()
            writer = new PrintWriter(new OutputStreamWriter(stream, 'UTF-8'))
            return writer
        }
    }

    void setContentLength(int i) {
    }

}