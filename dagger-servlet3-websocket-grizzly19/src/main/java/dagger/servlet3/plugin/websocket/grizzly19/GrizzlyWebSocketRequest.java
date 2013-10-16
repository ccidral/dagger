package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.util.buf.MessageBytes;
import com.sun.grizzly.util.http.Cookies;
import com.sun.grizzly.util.http.ServerCookie;
import dagger.http.NullQueryString;
import dagger.http.QueryString;
import dagger.http.QueryStringParser;
import dagger.http.Request;
import dagger.servlet3.lang.ServletUri;
import dagger.servlet3.lang.ServletUriParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GrizzlyWebSocketRequest implements Request {

    public static final ByteArrayInputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[]{});

    private final com.sun.grizzly.tcp.Request grizzlyTcpRequest;
    private final String httpMethod;
    private final ServletUriParser servletUriParser;
    private final QueryStringParser queryStringParser;
    private Map<String,String> cookies;

    public GrizzlyWebSocketRequest(com.sun.grizzly.tcp.Request grizzlyTcpRequest, String httpMethod, ServletUriParser servletUriParser, QueryStringParser queryStringParser) {
        this.grizzlyTcpRequest = grizzlyTcpRequest;
        this.httpMethod = httpMethod;
        this.servletUriParser = servletUriParser;
        this.queryStringParser = queryStringParser;
    }

    private ServletUri parseUri() {
        String uri = grizzlyTcpRequest.requestURI().toString();
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
        return getCookies().get(name);
    }

    @Override
    public InputStream getInputStream() {
        return EMPTY_INPUT_STREAM;
    }

    private String stringFrom(MessageBytes messageBytes) {
        if(messageBytes == null || messageBytes.toString() == null)
            return null;
        return messageBytes.toString();
    }

    private synchronized Map<String, String> getCookies() {
        if(cookies == null) {
            cookies = readCookiesFrom(grizzlyTcpRequest);
        }
        return cookies;
    }

    private static Map<String, String> readCookiesFrom(com.sun.grizzly.tcp.Request grizzlyTcpRequest) {
        Cookies cookies = grizzlyTcpRequest.getCookies();
        Map<String, String> map = new HashMap<String, String>();
        for(int index = 0; index < cookies.getCookieCount(); index++) {
            ServerCookie cookie = cookies.getCookie(index);
            String name = cookie.getName().getString();
            String value = cookie.getValue().getString();
            map.put(name, value);
        }
        return map;
    }

}
