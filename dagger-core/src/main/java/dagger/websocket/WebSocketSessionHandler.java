package dagger.websocket;

public interface WebSocketSessionHandler {

    void onOpen(WebSocketSession session);

    void onClose(WebSocketSession session);

    void onMessage(String message, WebSocketSession session);

}
