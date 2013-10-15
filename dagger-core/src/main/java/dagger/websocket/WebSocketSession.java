package dagger.websocket;

import dagger.http.StatusCode;

public interface WebSocketSession {

    void write(String message);

    void close();

    void close(StatusCode statusCode);

}
