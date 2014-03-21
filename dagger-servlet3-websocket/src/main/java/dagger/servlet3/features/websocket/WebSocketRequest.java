package dagger.servlet3.features.websocket;

import dagger.http.HttpHeader;
import dagger.http.QueryString;
import dagger.http.QueryStringImpl;
import dagger.http.Request;
import dagger.servlet3.uri.ServletURI;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketRequest implements Request {

    private final String httpMethod;
    private final InputStream inputStream;
    private final ServletURI servletUri;
    private final Map<String, List<String>> headers;

    private Map<String,String> cookies;

    public WebSocketRequest(String httpMethod, InputStream inputStream, ServletURI servletUri, Map<String, List<String>> headers) {
        this.httpMethod = httpMethod;
        this.inputStream = inputStream;
        this.servletUri = servletUri;
        this.headers = copyWithLowcaseKeys(headers);
    }

    private static HashMap<String, List<String>> copyWithLowcaseKeys(Map<String, List<String>> sourceMap) {
        HashMap<String, List<String>> resultMap = new HashMap<>();
        for(String key : sourceMap.keySet())
            resultMap.put(key.toLowerCase(), sourceMap.get(key));
        return resultMap;
    }

    @Override
    public String getContextPath() {
        return servletUri.getContextPath();
    }

    @Override
    public String getURI() {
        return servletUri.getResourcePath();
    }

    @Override
    public String getMethod() {
        return httpMethod;
    }

    @Override
    public QueryString getQueryString() {
        return new QueryStringImpl(null);
    }

    @Override
    public String getHeader(String name) {
        List<String> values = headers.get(name.toLowerCase());
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getCookie(String name) {
        return getCookies().get(name);
    }

    private Map<String, String> getCookies() {
        synchronized (this) {
            if(cookies == null)
                cookies = extractCookiesFromHeaders();
        }
        return cookies;
    }

    private Map<String, String> extractCookiesFromHeaders() {
        Map<String, String> map = new HashMap<>();
        String cookieHeader = getHeader(HttpHeader.COOKIE);

        if(cookieHeader != null) {
            String cookieList[] = cookieHeader.split("; *");
            for(String cookie : cookieList) {
                String nameAndValue[] = cookie.split("=");
                String name = nameAndValue[0];
                String value = nameAndValue[1];
                map.put(name, value);
            }
        }

        return map;
    }

}
