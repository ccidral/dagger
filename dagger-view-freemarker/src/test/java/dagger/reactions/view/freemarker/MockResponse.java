package dagger.reactions.view.freemarker;

import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.lang.NotImplementedYet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class MockResponse implements Response {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public StatusCode getStatusCode() {
        throw new NotImplementedYet();
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        throw new NotImplementedYet();
    }

    @Override
    public String getContentType() {
        throw new NotImplementedYet();
    }

    @Override
    public void setContentType(String contentType) {
        throw new NotImplementedYet();
    }

    public String getOutputAsString() {
        return new String(outputStream.toByteArray());
    }

}
