package dagger.server.netty;

import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;
import io.netty.channel.Channel;

import java.io.OutputStream;

public class NettyWebSocketResponse implements Response {

    private final OutputStream outputStream;
    private StatusCode statusCode = StatusCode.OK;

    public NettyWebSocketResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public NettyWebSocketResponse(Channel channel) {
        this.outputStream = new WebSocketOutputStream(channel);
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public void addCookie(Cookie cookie) {
    }

}
