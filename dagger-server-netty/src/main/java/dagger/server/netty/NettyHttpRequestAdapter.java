package dagger.server.netty;

import dagger.http.Request;
import io.netty.handler.codec.http.HttpRequest;

public class NettyHttpRequestAdapter implements Request {

    private final HttpRequest request;

    public NettyHttpRequestAdapter(HttpRequest request) {
        this.request = request;
    }

    @Override
    public String getResource() {
        return request.getUri();
    }

    @Override
    public String getMethod() {
        return request.getMethod().getName();
    }
}
