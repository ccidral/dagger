package dagger.server.netty;

import dagger.http.Request;
import dagger.lang.NotImplementedYet;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Collections;
import java.util.Map;

public class NettyRequest implements Request {

    private final HttpRequest request;

    public NettyRequest(HttpRequest request) {
        this.request = request;
    }

    @Override
    public String getURI() {
        String uri = request.getUri();

        if(uri.indexOf('?') > -1)
            return uri.substring(0, uri.indexOf('?'));

        return uri;
    }

    @Override
    public String getMethod() {
        return request.getMethod().getName();
    }

    @Override
    public Map<String, String> getParameters() {
        QueryString queryString = QueryString.fromUri(request.getUri());
        return Collections.unmodifiableMap(queryString.map());
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

}
