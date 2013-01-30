package dagger.reactions.view.freemarker;

import dagger.http.cookie.Cookie;
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
    public void setHeader(String name, String value) {
        throw new NotImplementedYet();
    }

    @Override
    public void setCookie(Cookie cookie) {
        throw new NotImplementedYet();
    }

    public String getOutputAsString() {
        return new String(outputStream.toByteArray());
    }

}
