package dagger.results;

import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class MockResponse implements Response {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private StatusCode statusCode;

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getWrittenOutput() {
        return new String(outputStream.toByteArray());
    }

}
