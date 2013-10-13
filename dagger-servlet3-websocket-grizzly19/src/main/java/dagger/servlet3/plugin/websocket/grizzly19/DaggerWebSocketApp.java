package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.*;
import dagger.DaggerRuntimeException;
import dagger.Module;
import dagger.RequestHandler;
import dagger.handlers.ResourceNotFound;
import dagger.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DaggerWebSocketApp extends WebSocketApplication {

    private final Module module;
    private final RequestFactory requestFactory;
    private final WebSocketFactory webSocketFactory;
    private final WebSocketListenerFactory webSocketListenerFactory;

    public DaggerWebSocketApp(Module module, RequestFactory requestFactory, WebSocketFactory webSocketFactory, WebSocketListenerFactory webSocketListenerFactory) {
        this.module = module;
        this.requestFactory = requestFactory;
        this.webSocketFactory = webSocketFactory;
        this.webSocketListenerFactory = webSocketListenerFactory;
    }

    @Override
    public boolean isApplicationRequest(Request grizzlyRequest) {
        dagger.http.Request request = requestFactory.createFrom(grizzlyRequest, HttpMethod.WEBSOCKET_OPEN);

        if(!isWebSocketRequest(request))
            return false;

        RequestHandler handler = module.getHandlerFor(request);

        return isHandlerAbleToHandleRequests(handler);
    }

    @Override
    public WebSocket createWebSocket(ProtocolHandler protocolHandler, WebSocketListener... listeners) {
        WebSocket webSocket = webSocketFactory.create(protocolHandler, listeners);
        HttpServletRequest httpServletRequest = getServletRequestFrom(protocolHandler);
        listenTo(webSocket, httpServletRequest);
        return webSocket;
    }

    private HttpServletRequest getServletRequestFrom(ProtocolHandler protocolHandler) {
        ServerNetworkHandler networkHandler = (ServerNetworkHandler) protocolHandler.getNetworkHandler();
        try {
            return networkHandler.getRequest();
        } catch (IOException e) {
            throw new DaggerRuntimeException(e);
        }
    }

    private void listenTo(WebSocket webSocket, HttpServletRequest servletRequest) {
        WebSocketListener listener = webSocketListenerFactory.create(module, servletRequest);
        webSocket.add(listener);
    }

    private boolean isHandlerAbleToHandleRequests(RequestHandler handler) {
        return handler != null && !(handler instanceof ResourceNotFound);
    }

    private boolean isWebSocketRequest(dagger.http.Request daggerRequest) {
        String upgradeHeader = daggerRequest.getHeader("Upgrade");
        return upgradeHeader != null && upgradeHeader.equals("websocket");
    }

}
