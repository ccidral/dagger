package dagger.servlet3.features.websocket;

import dagger.Module;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class DaggerEndpointConfigurator extends ServerEndpointConfig.Configurator {

    public static final String REQUEST_HEADERS_KEY = HttpServletRequest.class.getName() + ".headers";

    private final Module module;
    private final WebSocketRequestFactory requestFactory;
    private final WebSocketResponseFactory responseFactory;

    public DaggerEndpointConfigurator(Module module) {
        this.module = module;
        this.requestFactory = new DefaultWebSocketRequestFactory();
        this.responseFactory = new DefaultWebSocketResponseFactory();
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return (T) new DaggerEndpoint(module, requestFactory, responseFactory);
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig serverEndpointConfig, HandshakeRequest request, HandshakeResponse response) {
        serverEndpointConfig.getUserProperties().put(REQUEST_HEADERS_KEY, request.getHeaders());
        super.modifyHandshake(serverEndpointConfig, request, response);
    }

}
