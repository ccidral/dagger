package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.util.buf.MessageBytes;
import dagger.http.NullQueryString;
import dagger.http.QueryString;
import dagger.http.QueryStringParser;
import dagger.http.Request;
import dagger.servlet3.lang.ServletUri;
import dagger.servlet3.lang.ServletUriParser;

import java.io.InputStream;

public class GrizzlyRequest implements Request {

    private final com.sun.grizzly.tcp.Request grizzlyTcpRequest;
    private final String httpMethod;
    private final ServletUriParser servletUriParser;
    private final QueryStringParser queryStringParser;

    public GrizzlyRequest(com.sun.grizzly.tcp.Request grizzlyTcpRequest, String httpMethod, ServletUriParser servletUriParser, QueryStringParser queryStringParser) {
        this.grizzlyTcpRequest = grizzlyTcpRequest;
        this.httpMethod = httpMethod;
        this.servletUriParser = servletUriParser;
        this.queryStringParser = queryStringParser;
    }

    private ServletUri parseUri() {
        String uri = grizzlyTcpRequest.requestURI().getString();
        return servletUriParser.parse(uri);
    }

    @Override
    public String getContextPath() {
        return parseUri().getContextPath();
    }

    @Override
    public String getURI() {
        return parseUri().getResourcePath();
    }

    @Override
    public String getMethod() {
        return httpMethod;
    }

    @Override
    public QueryString getQueryString() {
        String queryString = stringFrom(grizzlyTcpRequest.queryString());
        if(queryString == null)
            return new NullQueryString();
        return queryStringParser.parse(queryString);
    }

    @Override
    public String getHeader(String name) {
        return grizzlyTcpRequest.getHeader(name);
    }

    @Override
    public String getCookie(String name) {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    private String stringFrom(MessageBytes messageBytes) {
        if(messageBytes == null || messageBytes.getString() == null)
            return null;
        return messageBytes.getString();
    }

}
