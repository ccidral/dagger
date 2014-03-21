package dagger.servlet3.features.websocket;

import dagger.http.Response;

import javax.websocket.Session;

public class DefaultWebSocketResponseFactory implements WebSocketResponseFactory {

    @Override
    public Response create(Session session) {
        return new WebSocketResponse(session);
    }

}
