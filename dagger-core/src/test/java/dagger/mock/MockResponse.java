package dagger.mock;

import dagger.lang.NotImplementedYet;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MockResponse implements Response {

    private StatusCode statusCode;
    private InMemoryOutputStream outputStream = new InMemoryOutputStream();
    private String contentType;

    @Override
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    private static class InMemoryOutputStream extends ByteArrayOutputStream {

        public boolean isClosed;

        @Override
        public void close() throws IOException {
            super.close();
            this.isClosed = true;
        }

    }

}
