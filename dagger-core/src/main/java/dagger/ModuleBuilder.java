package dagger;

import dagger.websocket.WebSocketSessionHandler;

public interface ModuleBuilder {

    void get(String route, Action action);

    void put(String route, Action action);

    void post(String route, Action action);

    void websocket(String route, WebSocketSessionHandler handler);

    /**
     * Warning: WebSocket support is experimental.
     */
    @Deprecated
    void wsopen(String route, Action action);

    /**
     * Warning: WebSocket support is experimental.
     */
    @Deprecated
    void wsmessage(String route, Action action);

    /**
     * Warning: WebSocket support is experimental.
     */
    @Deprecated
    void wsclose(String route, Action action);

}
