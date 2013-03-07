package dagger.server.netty;

import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;
import dagger.lang.NotImplementedYet;

import java.io.OutputStream;

public class NettyWebSocketResponse implements Response {

    private final OutputStream outputStream;

    public NettyWebSocketResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

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

}
