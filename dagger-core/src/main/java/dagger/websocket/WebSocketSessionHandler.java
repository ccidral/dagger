package dagger.websocket;

import dagger.http.Request;

public interface WebSocketSessionHandler {

    void onOpen(Request request, WebSocketOutput output);

    void onClose(Request request);

    void onMessage(Request request, WebSocketOutput output, String message);

}
