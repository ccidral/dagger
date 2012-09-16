package dagger.reactions;

import dagger.lang.NotImplementedYet;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class MockResponse implements Response {

    private StatusCode statusCode;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Override
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setContentType(String contentType) {
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

}
