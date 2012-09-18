package dagger.mock;

import dagger.lang.NotImplementedYet;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class MockResponse implements Response {

    private StatusCode statusCode;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
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

}
