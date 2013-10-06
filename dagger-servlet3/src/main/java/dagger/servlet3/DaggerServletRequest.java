package dagger.servlet3;

import dagger.DaggerRuntimeException;
import dagger.http.QueryString;
import dagger.http.QueryStringImpl;
import dagger.http.Request;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DaggerServletRequest implements Request {

    private final HttpServletRequest httpServletRequest;
    private Map<String, String> cookies;

    public DaggerServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public String getContextPath() {
        return httpServletRequest.getContextPath();
    }

    @Override
    public String getURI() {
        String contextPath = httpServletRequest.getContextPath();
        String requestURI = httpServletRequest.getRequestURI();
        if(contextPath == null || contextPath.equals("")) {
            return requestURI;
        }

        return requestURI.substring(contextPath.length());
    }

    @Override
    public String getMethod() {
        return httpServletRequest.getMethod();
    }

    @Override
    public QueryString getQueryString() {
        return new QueryStringImpl(httpServletRequest.getQueryString());
    }

    @Override
    public String getHeader(String name) {
        return httpServletRequest.getHeader(name);
    }

    @Override
    public synchronized String getCookie(String name) {
        if(cookies == null)
            cookies = getCookiesFromServletRequest();

        return cookies.get(name);
    }

    @Override
    public InputStream getInputStream() {
        try {
            return httpServletRequest.getInputStream();
        } catch (IOException e) {
            throw new DaggerRuntimeException(e);
        }
    }

    private Map<String, String> getCookiesFromServletRequest() {
        Map<String, String> map = new HashMap<>();
        if(httpServletRequest.getCookies() != null) {
            for(Cookie cookie : httpServletRequest.getCookies()) {
                map.put(cookie.getName(), cookie.getValue());
            }
        }
        return map;
    }

}
