package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocket;
import dagger.http.Response;

public interface ResponseFactory {

    Response create(WebSocket webSocket);

}
