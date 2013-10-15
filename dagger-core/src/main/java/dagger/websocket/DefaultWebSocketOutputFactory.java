package dagger.websocket;

import dagger.http.Response;

public class DefaultWebSocketOutputFactory implements WebSocketOutputFactory {

    @Override
    public WebSocketSession create(Response response) {
        return new DefaultWebSocketSession(response);
    }

}
