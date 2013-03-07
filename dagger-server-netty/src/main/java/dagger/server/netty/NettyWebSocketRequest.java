package dagger.server.netty;

import dagger.http.QueryString;
import dagger.http.QueryStringImpl;
import dagger.http.Request;
import dagger.lang.NotImplementedYet;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.InputStream;

public class NettyWebSocketRequest implements Request {

    private final TextWebSocketFrame frame;
    private final FullHttpRequest httpRequest;

    public NettyWebSocketRequest(TextWebSocketFrame frame, FullHttpRequest httpRequest) {
        this.frame = frame;
        this.httpRequest = httpRequest;
    }

    @Override
    public String getURI() {
        String uri = httpRequest.getUri();
        if(uri.indexOf('?') > -1)
            return uri.substring(0, uri.indexOf('?'));
        return uri;
    }

    @Override
    public String getMethod() {
        return httpRequest.getMethod().name();
    }

    @Override
    public QueryString getQueryString() {
        return new QueryStringImpl("");
    }

    @Override
    public String getHeader(String name) {
        return httpRequest.headers().get(name);
    }

    @Override
    public String getCookie(String name) {
        throw new NotImplementedYet();
    }

    @Override
    public InputStream getBody() {
        return new ByteBufInputStream(frame.data());
    }

}
