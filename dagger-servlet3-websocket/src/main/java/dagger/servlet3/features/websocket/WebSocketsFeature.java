package dagger.servlet3.features.websocket;

import dagger.Module;
import dagger.servlet3.features.ServletFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

public class WebSocketsFeature implements ServletFeature {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void enable(ServletContext servletContext) {
        logger.info("Enabling feature");

        Module module = (Module) servletContext.getAttribute(Module.class.getName());
        ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute(ServerContainer.class.getName());

        ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder.create(DaggerEndpoint.class, "/{anyPath}")
            .configurator(new DaggerEndpointConfigurator(module))
            .build();

        try {
            serverContainer.addEndpoint(endpointConfig);
        } catch (DeploymentException e) {
            logger.error("Error configuring websocket endpoint", e);
        }
    }

}
