package dagger.websocket;

import dagger.http.Request;
import dagger.http.Response;

public class DefaultWebSocketSessionFactory implements WebSocketSessionFactory {

    @Override
    public WebSocketSession create(Request request, Response response) {
        return new DefaultWebSocketSession(request, response);
    }

}
