package dagger.server.netty;

import dagger.http.QueryString;
import dagger.http.QueryStringImpl;
import dagger.http.Request;
import dagger.http.cookie.CookieParser;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.InputStream;
import java.util.Map;

public class NettyRequest implements Request {

    private final String host;
    private final int port;
    private final FullHttpRequest request;

    public NettyRequest(String host, int port, FullHttpRequest request) {
        this.host = host;
        this.port = port;
        this.request = request;
    }

    @Override
    public String getRequestURL() {
        return "http://" + host + (port != 80 ? ":" + port : "") + request.getUri();
    }

    @Override
    public String getContextPath() {
        return "";
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
        return request.getMethod().name();
    }

    @Override
    public QueryString getQueryString() {
        return QueryStringImpl.fromUri(request.getUri());
    }

    @Override
    public String getHeader(String name) {
        return request.headers().get(name);
    }

    @Override
    public String getCookie(String name) {
        String cookieString = request.headers().get("Cookie");
        Map<String, String> cookiesMap = new CookieParser().parseCookies(cookieString);
        if(cookiesMap == null)
            return null;
        return cookiesMap.get(name);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteBufInputStream(request.data());
    }

}
