package dagger.websocket;

import dagger.http.Response;

public interface WebSocketOutputFactory {

    WebSocketOutput create(Response response);

}
