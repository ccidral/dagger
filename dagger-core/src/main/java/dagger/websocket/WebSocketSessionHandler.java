package dagger.websocket;

import dagger.http.Request;

public interface WebSocketSessionHandler {

    void onOpen(Request request, WebSocketSession session);

    void onClose(Request request);

    void onMessage(Request request, WebSocketSession session, String message);

}
