package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocketEngine;
import dagger.Module;
import dagger.http.DefaultQueryStringParser;
import dagger.http.QueryStringParser;
import dagger.servlet3.lang.ServletUriParser;
import dagger.servlet3.lang.ServletUriParserImpl;
import dagger.servlet3.plugin.DaggerServletPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;

public class GrizzlyWebSocketPlugin implements DaggerServletPlugin {

    private DaggerWebSocketApp webSocketApp;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void initialize(Module module, ServletConfig servletConfig) {
        logger.info("Creating websocket app");

        ServletUriParser servletUriParser = new ServletUriParserImpl();
        QueryStringParser queryStringParser = new DefaultQueryStringParser();
        RequestFactory requestFactory = new DefaultRequestFactory(servletUriParser, queryStringParser);
        WebSocketFactory webSocketFactory = new WebSocketFactoryImpl();
        ResponseFactory responseFactory = new DefaultResponseFactory();
        WebSocketListenerFactory webSocketListenerFactory = new WebSocketListenerFactoryImpl(requestFactory, responseFactory);
        webSocketApp = new DaggerWebSocketApp(module, requestFactory, webSocketFactory, webSocketListenerFactory);

        logger.info("Registering websocket app");
        WebSocketEngine.getEngine().register(webSocketApp);
    }

    @Override
    public void destroy() {
        logger.info("Unregistering websocket app");
        WebSocketEngine.getEngine().unregister(webSocketApp);
    }

}
