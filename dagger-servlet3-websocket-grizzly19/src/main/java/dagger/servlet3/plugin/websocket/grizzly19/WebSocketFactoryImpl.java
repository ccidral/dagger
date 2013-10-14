package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.DefaultWebSocket;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketListener;

public class WebSocketFactoryImpl implements WebSocketFactory {

    @Override
    public WebSocket create(ProtocolHandler protocolHandler, WebSocketListener[] webSocketListeners) {
        return new DefaultWebSocket(protocolHandler, webSocketListeners);
    }

}
