package dagger.server.netty;

import dagger.http.QueryString;
import dagger.http.QueryStringImpl;
import dagger.http.Request;
import dagger.http.cookie.CookieParser;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public class NettyWebSocketRequest implements Request {

    private final String message;
    private final String method;
    private final FullHttpRequest httpRequest;

    public NettyWebSocketRequest(String message, String method, FullHttpRequest httpRequest) {
        this.message = message;
        this.method = method;
        this.httpRequest = httpRequest;
    }

    @Override
    public String getContextPath() {
        return "";
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
        return method;
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
        String cookieString = httpRequest.headers().get("Cookie");
        Map<String, String> cookiesMap = new CookieParser().parseCookies(cookieString);
        if(cookiesMap == null)
            return null;
        return cookiesMap.get(name);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(message.getBytes());
    }

}
