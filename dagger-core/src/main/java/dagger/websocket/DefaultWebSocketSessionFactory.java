package dagger.websocket;

import dagger.http.Response;

public class DefaultWebSocketSessionFactory implements WebSocketSessionFactory {

    @Override
    public WebSocketSession create(Response response) {
        return new DefaultWebSocketSession(response);
    }

}
