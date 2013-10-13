package dagger.handlers;

import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.HttpMethod;
import dagger.http.Response;
import dagger.http.UnexpectedHttpMethodException;
import dagger.lang.io.Streams;
import dagger.websocket.WebSocketOutput;
import dagger.websocket.WebSocketOutputFactory;
import dagger.websocket.WebSocketSessionHandler;
import dagger.http.Request;

import java.util.HashMap;
import java.util.Map;

public class WebSocket implements RequestHandler {

    private final Route route;
    private final WebSocketSessionHandler webSocketSessionHandler;
    private final WebSocketOutputFactory webSocketOutputFactory;
    private final Map<String, Reaction> possibleReactions;

    public WebSocket(Route route, WebSocketSessionHandler webSocketSessionHandler, WebSocketOutputFactory webSocketOutputFactory) {
        this.route = route;
        this.webSocketSessionHandler = webSocketSessionHandler;
        this.webSocketOutputFactory = webSocketOutputFactory;
        this.possibleReactions = new HashMap<>();

        possibleReactions.put(HttpMethod.WEBSOCKET_OPEN, new TriggerOnWebSocketOpen());
        possibleReactions.put(HttpMethod.WEBSOCKET_CLOSE, new TriggerOnWebSocketClose());
        possibleReactions.put(HttpMethod.WEBSOCKET_MESSAGE, new TriggerOnWebSocketMessage());
    }

    public Route getRoute() {
        return route;
    }

    public WebSocketSessionHandler getSessionHandler() {
        return webSocketSessionHandler;
    }

    public WebSocketOutputFactory getWebSocketOutputFactory() {
        return webSocketOutputFactory;
    }

    @Override
    public boolean canHandle(Request request) {
        return isWebSocketRequest(request) && route.matches(request.getURI());
    }

    @Override
    public Reaction handle(Request request) throws Exception {
        Reaction reaction = possibleReactions.get(request.getMethod());

        if(reaction == null)
            throw new UnexpectedHttpMethodException(request.getMethod());

        return reaction;
    }

    private boolean isWebSocketRequest(Request request) {
        return possibleReactions.get(request.getMethod()) != null;
    }

    private class TriggerOnWebSocketOpen implements Reaction {
        @Override
        public void execute(Request request, Response response) throws Exception {
            WebSocketOutput webSocketOutput = webSocketOutputFactory.create(response);
            webSocketSessionHandler.onOpen(request, webSocketOutput);
        }
    }

    private class TriggerOnWebSocketClose implements Reaction {
        @Override
        public void execute(Request request, Response response) throws Exception {
            webSocketSessionHandler.onClose(request);
        }
    }

    private class TriggerOnWebSocketMessage implements Reaction {
        @Override
        public void execute(Request request, Response response) throws Exception {
            WebSocketOutput webSocketOutput = webSocketOutputFactory.create(response);
            String message = Streams.toString(request.getInputStream());
            webSocketSessionHandler.onMessage(request, webSocketOutput, message);
        }
    }

}
