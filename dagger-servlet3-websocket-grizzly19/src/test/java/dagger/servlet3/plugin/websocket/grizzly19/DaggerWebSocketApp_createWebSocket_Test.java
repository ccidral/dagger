package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.*;
import dagger.Module;
import dagger.servlet3.plugin.websocket.grizzly19.DaggerWebSocketApp;
import dagger.servlet3.plugin.websocket.grizzly19.WebSocketFactory;
import dagger.servlet3.plugin.websocket.grizzly19.WebSocketListenerFactory;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class DaggerWebSocketApp_createWebSocket_Test {

    private Module module;
    private WebSocketFactory webSocketFactory;
    private WebSocketListenerFactory webSocketListenerFactory;
    private WebSocketApplication webSocketApp;

    @Before
    public void setUp() throws Exception {
        module = mock(Module.class);
        webSocketFactory = mock(WebSocketFactory.class);
        webSocketListenerFactory = mock(WebSocketListenerFactory.class);
        webSocketApp = new DaggerWebSocketApp(module, null, webSocketFactory, webSocketListenerFactory);
    }

    @Test
    public void test_create_websocket() throws Throwable {
        ProtocolHandler protocolHandler = createProtocolHandler();
        WebSocketListener[] anyOtherWebSocketListeners = new WebSocketListener[]{mock(WebSocketListener.class), mock(WebSocketListener.class)};

        WebSocket webSocket = given_that_websocket_factory_will_create_a_websocket_with(protocolHandler, anyOtherWebSocketListeners);
        WebSocket createdWebSocket = webSocketApp.createWebSocket(protocolHandler, anyOtherWebSocketListeners);

        assertSame(webSocket, createdWebSocket);
    }

    @Test
    public void test_add_listener_to_websocket_when_it_is_created() throws Throwable {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        WebSocketListener[] anyOtherWebSocketListeners = new WebSocketListener[]{mock(WebSocketListener.class), mock(WebSocketListener.class)};

        ProtocolHandler protocolHandler = createProtocolHandlerWith(httpServletRequest);
        WebSocket webSocket = given_that_websocket_factory_will_create_a_websocket_with(protocolHandler, anyOtherWebSocketListeners);
        WebSocketListener listener = given_that_a_listener_can_be_created_with(httpServletRequest);

        webSocketApp.createWebSocket(protocolHandler, anyOtherWebSocketListeners);

        verify(webSocket).add(listener);
    }

    private ProtocolHandler createProtocolHandler() throws IOException {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        return createProtocolHandlerWith(httpServletRequest);
    }

    private static ProtocolHandler createProtocolHandlerWith(HttpServletRequest httpServletRequest) throws IOException {
        ProtocolHandler protocolHandler = mock(ProtocolHandler.class);
        ServerNetworkHandler networkHandler = mock(ServerNetworkHandler.class);
        when(protocolHandler.getNetworkHandler()).thenReturn(networkHandler);
        when(networkHandler.getRequest()).thenReturn(httpServletRequest);
        return protocolHandler;
    }

    private WebSocketListener given_that_a_listener_can_be_created_with(HttpServletRequest httpServletRequest) {
        WebSocketListener listener = mock(WebSocketListener.class);
        when(webSocketListenerFactory.create(module, httpServletRequest)).thenReturn(listener);
        return listener;
    }

    private WebSocket given_that_websocket_factory_will_create_a_websocket_with(ProtocolHandler protocolHandler, WebSocketListener[] webSocketListeners) {
        WebSocket webSocket = mock(WebSocket.class);
        when(webSocketFactory.create(protocolHandler, webSocketListeners)).thenReturn(webSocket);
        return webSocket;
    }

}
