package dagger.mock;

import dagger.http.cookie.Cookie;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.lang.NotImplementedYet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MockResponse implements Response {

    private StatusCode statusCode;
    private InMemoryOutputStream outputStream = new InMemoryOutputStream();
    private Map<String, String> headers = new HashMap<>();

    @Override
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void setCookie(Cookie cookie) {
        throw new NotImplementedYet();
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getOutputAsString() {
        return new String(outputStream.toByteArray());
    }

    public boolean isOutputStreamClosed() {
        return outputStream.isClosed;
    }

    public byte[] getOutputAsBytes() {
        return outputStream.toByteArray();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    private static class InMemoryOutputStream extends ByteArrayOutputStream {

        public boolean isClosed;

        @Override
        public void close() throws IOException {
            super.close();
            this.isClosed = true;
        }

    }

}
