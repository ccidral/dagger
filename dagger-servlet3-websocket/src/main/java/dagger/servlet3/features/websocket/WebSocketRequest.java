package dagger.servlet3.features.websocket;

import dagger.http.QueryString;
import dagger.http.QueryStringImpl;
import dagger.http.Request;
import dagger.servlet3.uri.ServletUri;

import java.io.InputStream;

public class WebSocketRequest implements Request {

    private final String httpMethod;
    private final InputStream inputStream;
    private final ServletUri servletUri;

    public WebSocketRequest(String httpMethod, InputStream inputStream, ServletUri servletUri) {
        this.httpMethod = httpMethod;
        this.inputStream = inputStream;
        this.servletUri = servletUri;
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
        return null;
    }

    @Override
    public String getCookie(String name) {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

}
