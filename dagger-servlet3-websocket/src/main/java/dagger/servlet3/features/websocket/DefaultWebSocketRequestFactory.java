package dagger.servlet3.features.websocket;

import dagger.http.Request;
import dagger.servlet3.uri.DefaultServletURI;
import dagger.servlet3.uri.ServletURI;

import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class DefaultWebSocketRequestFactory implements WebSocketRequestFactory {

    @Override
    public Request create(String httpMethod, String requestBody, Session session) {
        InputStream inputStream = new ByteArrayInputStream(requestBody != null ? requestBody.getBytes() : new byte[]{});
        ServletURI servletUri = new DefaultServletURI(session.getRequestURI().toString());
        Map<String, List<String>> headers = (Map<String, List<String>>) session.getUserProperties().get(DaggerEndpointConfigurator.REQUEST_HEADERS_KEY);
        return new WebSocketRequest(httpMethod, inputStream, servletUri, headers);
    }

}
