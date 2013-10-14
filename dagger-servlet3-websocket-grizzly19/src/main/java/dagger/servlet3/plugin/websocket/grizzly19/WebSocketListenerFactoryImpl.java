package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocketListener;
import dagger.Module;

import javax.servlet.http.HttpServletRequest;

public class WebSocketListenerFactoryImpl implements WebSocketListenerFactory {

    private final RequestFactory requestFactory;
    private final ResponseFactory responseFactory;

    public WebSocketListenerFactoryImpl(RequestFactory requestFactory, ResponseFactory responseFactory) {
        this.requestFactory = requestFactory;
        this.responseFactory = responseFactory;
    }

    @Override
    public WebSocketListener create(Module module, HttpServletRequest httpServletRequest) {
        return new DaggerWebSocketListener(module, httpServletRequest, requestFactory, responseFactory);
    }

}
