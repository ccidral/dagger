package dagger.websocket;

import dagger.http.Response;

public interface WebSocketSessionFactory {

    WebSocketSession create(Response response);

}
