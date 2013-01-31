package dagger.server.netty;

import dagger.http.QueryString;
import dagger.http.QueryStringImpl;
import dagger.http.Request;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.HttpRequest;

import java.io.InputStream;
import java.util.HashMap;
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
    public QueryString getQueryString() {
        return QueryStringImpl.fromUri(request.getUri());
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public String getCookie(String name) {
        String cookieString = request.getHeader("Cookie");
        Map<String, String> cookiesMap = parseCookies(cookieString);
        if(cookiesMap == null)
            return null;
        return cookiesMap.get(name);
    }

    @Override
    public InputStream getBody() {
        return new ByteBufInputStream(request.getContent());
    }

    private Map<String, String> parseCookies(String cookiesString) {
        if(cookiesString == null)
            return null;

        Map<String, String> map = new HashMap<>();
        for(String cookie : cookiesString.split("; ")) {
            String[] keyValue = cookie.split("=");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }

}
