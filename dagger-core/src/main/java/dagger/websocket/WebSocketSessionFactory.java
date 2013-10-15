package dagger.websocket;

import dagger.http.Request;
import dagger.http.Response;

public interface WebSocketSessionFactory {

    WebSocketSession create(Request request, Response response);

}
