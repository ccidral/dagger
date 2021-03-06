package dagger.module;

import dagger.*;
import dagger.handlers.*;
import dagger.http.HttpMethod;
import dagger.websocket.DefaultWebSocketSessionFactory;
import dagger.websocket.WebSocketSessionFactory;
import dagger.websocket.WebSocketSessionHandler;

public class DefaultModuleBuilder implements ModuleBuilder {

    private final Module module;
    private final RouteFactory routeFactory;
    private final WebSocketSessionFactory webSocketSessionFactory;

    public DefaultModuleBuilder(Module module, RouteFactory routeFactory) {
        this(module, routeFactory, new DefaultWebSocketSessionFactory());
    }

    public DefaultModuleBuilder(Module module, RouteFactory routeFactory, WebSocketSessionFactory webSocketSessionFactory) {
        this.module = module;
        this.routeFactory = routeFactory;
        this.webSocketSessionFactory = webSocketSessionFactory;
    }

    @Override
    public void get(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new HttpMethodRequestHandler(HttpMethod.GET, route, action));
    }

    @Override
    public void put(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new HttpMethodRequestHandler(HttpMethod.PUT, route, action));
    }

    @Override
    public void post(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new HttpMethodRequestHandler(HttpMethod.POST, route, action));
    }

    @Override
    public void delete(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new HttpMethodRequestHandler(HttpMethod.DELETE, route, action));
    }

    @Override
    public void websocket(String routeSpecification, WebSocketSessionHandler sessionHandler) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new WebSocket(route, sessionHandler, webSocketSessionFactory));
    }

}
