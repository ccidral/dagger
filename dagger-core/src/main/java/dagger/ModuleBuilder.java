package dagger;

import dagger.websocket.WebSocketSessionHandler;

public interface ModuleBuilder {

    void get(String route, Action action);

    void put(String route, Action action);

    void post(String route, Action action);

    void websocket(String route, WebSocketSessionHandler handler);

}
