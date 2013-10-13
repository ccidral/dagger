package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocketEngine;
import dagger.Module;
import dagger.servlet3.plugin.DaggerServletPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;

public class GrizzlyWebSocketPlugin implements DaggerServletPlugin {

    private DaggerWebSocketApp webSocketApp;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void initialize(Module module, ServletConfig servletConfig) {
        logger.info("Creating websocket app");

        //TODO
        //webSocketApp = new DaggerWebSocketApp(module, requestConversor);

        logger.info("Registering websocket app");
        WebSocketEngine.getEngine().register(webSocketApp);
    }

    @Override
    public void destroy() {
        logger.info("Unregistering websocket app");
        WebSocketEngine.getEngine().unregister(webSocketApp);
    }

}
