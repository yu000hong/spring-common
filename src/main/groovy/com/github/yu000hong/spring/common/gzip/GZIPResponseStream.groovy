package com.github.yu000hong.spring.common.gzip

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import java.util.zip.GZIPOutputStream

/**
 *
 * @author OpCAT
 * @07-May-2013 com.opcat.gzip
 */
class GZIPResponseStream extends ServletOutputStream {
    protected ByteArrayOutputStream baos
    protected GZIPOutputStream gzipStream
    protected boolean closed
    protected HttpServletResponse response
    protected ServletOutputStream output

    GZIPResponseStream(HttpServletResponse httpServletResponse)
            throws IOException {
        baos = null
        gzipStream = null
        closed = false
        response = null
        output = null
        closed = false
        response = httpServletResponse
        output = httpServletResponse.outputStream
        baos = new ByteArrayOutputStream()
        gzipStream = new GZIPOutputStream(baos)
    }

    @Override
    void close() throws IOException {
        if (closed) {
            throw new IOException('This output stream has already been closed')
        } else {
            gzipStream.finish()
            byte[] bytes = baos.toByteArray()
            response.setHeader('Content-Length', String.valueOf(bytes.length))
            response.setHeader('Content-Encoding', 'gzip')
            output.write(bytes)
            output.flush()
            output.close()
            closed = true
        }
    }

    void flush() throws IOException {
        if (closed) {
            throw new IOException('Cannot flush a closed output stream')
        } else {
            gzipStream.flush()
        }
    }

    void write(int i) throws IOException {
        if (closed) {
            throw new IOException('Cannot write to a closed output stream')
        } else {
            gzipStream.write((byte) i)
        }
    }

    void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length)
    }

    void write(byte[] bytes, int offset, int len) throws IOException {
        if (closed) {
            throw new IOException('Cannot write to a closed output stream')
        } else {
            gzipStream.write(bytes, offset, len)
        }
    }

    boolean closed() {
        return closed
    }

    void reset() {
    }

}
