package dagger.websocket;

import dagger.http.Request;
import dagger.http.StatusCode;

public interface WebSocketSession {

    Request getRequest();

    void write(String message);

    void close();

    void close(StatusCode statusCode);

}
