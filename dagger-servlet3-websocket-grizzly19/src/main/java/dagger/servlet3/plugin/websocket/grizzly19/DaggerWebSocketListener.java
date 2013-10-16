package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketListener;
import dagger.DaggerRuntimeException;
import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import dagger.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class DaggerWebSocketListener implements WebSocketListener {

    private final Module module;
    private final HttpServletRequest httpServletRequest;
    private final RequestFactory requestFactory;
    private final ResponseFactory responseFactory;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DaggerWebSocketListener(Module module, HttpServletRequest httpServletRequest, RequestFactory requestFactory, ResponseFactory responseFactory) {
        this.module = module;
        this.httpServletRequest = httpServletRequest;
        this.requestFactory = requestFactory;
        this.responseFactory = responseFactory;
    }

    private void handleRequest(Request request, WebSocket webSocket) {
        Response response = responseFactory.create(webSocket);
        RequestHandler requestHandler = module.getHandlerFor(request);
        try {
            Reaction reaction = requestHandler.handle(request);
            reaction.execute(request, response);
        } catch (Exception e) {
            throw new DaggerRuntimeException(e);
        }
    }

    @Override
    public void onConnect(WebSocket webSocket) {
        logger.debug("Connected to '{}'", httpServletRequest.getRequestURI());
        Request request = requestFactory.createWebSocketOpenRequest(httpServletRequest);
        handleRequest(request, webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, DataFrame dataFrame) {
        logger.debug("Closed '{}'", httpServletRequest.getRequestURI());
        Request request = requestFactory.createWebSocketCloseRequest(httpServletRequest);
        handleRequest(request, webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        logger.debug("Text message to '{}': {}", httpServletRequest.getRequestURI(), message);
        Request request = requestFactory.createWebSocketMessageRequest(httpServletRequest, message);
        handleRequest(request, webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, byte[] bytes) {
        logger.debug("Binary message to '{}'", httpServletRequest.getRequestURI());
    }

    @Override
    public void onPing(WebSocket webSocket, byte[] bytes) {
        logger.debug("Ping '{}'", httpServletRequest.getRequestURI());
    }

    @Override
    public void onPong(WebSocket webSocket, byte[] bytes) {
        logger.debug("Pong '{}'", httpServletRequest.getRequestURI());
    }

    @Override
    public void onFragment(WebSocket webSocket, String s, boolean b) {
    }

    @Override
    public void onFragment(WebSocket webSocket, byte[] bytes, boolean b) {
    }

}
