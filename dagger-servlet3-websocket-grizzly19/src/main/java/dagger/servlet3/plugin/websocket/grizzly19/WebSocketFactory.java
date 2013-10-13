package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketListener;

public interface WebSocketFactory {

    WebSocket create(ProtocolHandler protocolHandler, WebSocketListener[] webSocketListeners);

}
