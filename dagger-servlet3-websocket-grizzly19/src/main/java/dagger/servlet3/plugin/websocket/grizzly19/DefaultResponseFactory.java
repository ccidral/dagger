package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocket;
import dagger.http.Response;

public class DefaultResponseFactory implements ResponseFactory {

    @Override
    public Response create(WebSocket webSocket) {
        return new GrizzlyWebSocketResponse(webSocket);
    }

}
