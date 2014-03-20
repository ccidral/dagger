package dagger.servlet3.features.websocket;

import dagger.DaggerRuntimeException;
import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.HttpMethod;
import dagger.http.Request;
import dagger.http.Response;

import javax.websocket.*;
import java.io.IOException;

import static dagger.servlet3.features.websocket.DaggerEndpointConfigurator.REQUEST_HEADERS_KEY;

public class DaggerEndpoint extends Endpoint {

    private final Module module;
    private final WebSocketRequestFactory requestFactory;
    private final WebSocketResponseFactory responseFactory;

    public DaggerEndpoint(Module module, WebSocketRequestFactory requestFactory, WebSocketResponseFactory responseFactory) {
        this.module = module;
        this.requestFactory = requestFactory;
        this.responseFactory = responseFactory;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        copyRequestHeadersFromEndpointConfigToSession(endpointConfig, session);

        boolean handledTheRequest = handleRequest(HttpMethod.WEBSOCKET_OPEN, null, session);

        if(handledTheRequest)
            session.addMessageHandler(new TextMessageHandler(session));
        else
            close(session);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        handleRequest(HttpMethod.WEBSOCKET_CLOSE, null, session);
    }

    private void copyRequestHeadersFromEndpointConfigToSession(EndpointConfig endpointConfig, Session session) {
        session.getUserProperties().put(REQUEST_HEADERS_KEY, endpointConfig.getUserProperties().get(REQUEST_HEADERS_KEY));
    }

    private boolean handleRequest(String httpMethod, String requestBody, Session session) {
        Request request = requestFactory.create(httpMethod, requestBody, session);
        RequestHandler requestHandler = module.getHandlerFor(request);
        boolean canHandle = requestHandler.canHandle(request);

        if(canHandle)
            handleRequest(request, requestHandler, session);

        return canHandle;
    }

    private void handleRequest(Request request, RequestHandler requestHandler, Session session) {
        try {
            Reaction reaction = requestHandler.handle(request);
            Response response = responseFactory.create(session);
            reaction.execute(request, response);
        } catch (Exception e) {
            throw new DaggerRuntimeException(e);
        }
    }

    private void close(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            throw new DaggerRuntimeException(e);
        }
    }

    private class TextMessageHandler implements MessageHandler.Whole<String> {

        private final Session session;

        public TextMessageHandler(Session session) {
            this.session = session;
        }

        @Override
        public void onMessage(String message) {
            handleRequest(HttpMethod.WEBSOCKET_MESSAGE, message, session);
        }

    }

}
