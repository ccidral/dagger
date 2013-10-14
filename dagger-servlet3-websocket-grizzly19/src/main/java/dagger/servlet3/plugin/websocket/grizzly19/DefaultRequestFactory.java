package dagger.servlet3.plugin.websocket.grizzly19;

import dagger.http.HttpMethod;
import dagger.http.QueryStringParser;
import dagger.http.Request;
import dagger.servlet3.DaggerServletRequest;
import dagger.servlet3.lang.ServletUriParser;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DefaultRequestFactory implements RequestFactory {

    private final ServletUriParser servletUriParser;
    private final QueryStringParser queryStringParser;

    public DefaultRequestFactory(ServletUriParser servletUriParser, QueryStringParser queryStringParser) {
        this.servletUriParser = servletUriParser;
        this.queryStringParser = queryStringParser;
    }

    @Override
    public Request createFrom(com.sun.grizzly.tcp.Request grizzlyRequest, String httpMethod) {
        return new GrizzlyWebSocketRequest(grizzlyRequest, httpMethod, servletUriParser, queryStringParser);
    }

    @Override
    public Request createWebSocketOpenRequest(HttpServletRequest httpServletRequest) {
        return new WebSocketRequest(httpServletRequest, HttpMethod.WEBSOCKET_OPEN);
    }

    @Override
    public Request createWebSocketCloseRequest(HttpServletRequest httpServletRequest) {
        return new WebSocketRequest(httpServletRequest, HttpMethod.WEBSOCKET_CLOSE);
    }

    @Override
    public Request createWebSocketMessageRequest(HttpServletRequest httpServletRequest, String message) {
        return new WebSocketRequest(httpServletRequest, HttpMethod.WEBSOCKET_MESSAGE, message);
    }

    private static class WebSocketRequest extends DaggerServletRequest {

        private final String httpMethod;
        private final ByteArrayInputStream inputStream;

        public WebSocketRequest(HttpServletRequest httpServletRequest, String httpMethod) {
            this(httpServletRequest, httpMethod, "");
        }

        public WebSocketRequest(HttpServletRequest httpServletRequest, String httpMethod, String message) {
            super(httpServletRequest);
            this.httpMethod = httpMethod;
            this.inputStream = new ByteArrayInputStream(message.getBytes());
        }

        @Override
        public String getMethod() {
            return httpMethod;
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }
    }
}
