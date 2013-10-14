package dagger.websocket;

import dagger.http.Response;

public class DefaultWebSocketOutputFactory implements WebSocketOutputFactory {

    @Override
    public WebSocketOutput create(Response response) {
        return new DefaultWebSocketOutput(response);
    }

}
