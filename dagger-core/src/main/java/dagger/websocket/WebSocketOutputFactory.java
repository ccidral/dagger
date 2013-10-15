package dagger.websocket;

import dagger.http.Response;

public interface WebSocketOutputFactory {

    WebSocketSession create(Response response);

}
