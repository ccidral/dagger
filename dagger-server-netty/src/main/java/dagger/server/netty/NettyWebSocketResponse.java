package dagger.server.netty;

import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;
import io.netty.channel.Channel;

import java.io.OutputStream;

public class NettyWebSocketResponse implements Response {

    private final OutputStream outputStream;

    public NettyWebSocketResponse(Channel channel) {
        this.outputStream = new WebSocketOutputStream(channel);
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public StatusCode getStatusCode() {
        return StatusCode.OK;
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public void setCookie(Cookie cookie) {
    }

}
