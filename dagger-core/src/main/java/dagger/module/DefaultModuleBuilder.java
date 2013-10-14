package dagger.module;

import dagger.*;
import dagger.handlers.*;
import dagger.websocket.DefaultWebSocketOutputFactory;
import dagger.websocket.WebSocketOutputFactory;
import dagger.websocket.WebSocketSessionHandler;

public class DefaultModuleBuilder implements ModuleBuilder {

    private final Module module;
    private final RouteFactory routeFactory;
    private final WebSocketOutputFactory webSocketOutputFactory;

    public DefaultModuleBuilder(Module module, RouteFactory routeFactory) {
        this(module, routeFactory, new DefaultWebSocketOutputFactory());
    }

    public DefaultModuleBuilder(Module module, RouteFactory routeFactory, WebSocketOutputFactory webSocketOutputFactory) {
        this.module = module;
        this.routeFactory = routeFactory;
        this.webSocketOutputFactory = webSocketOutputFactory;
    }

    @Override
    public void get(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new Get(route, action));
    }

    @Override
    public void put(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new Put(route, action));
    }

    @Override
    public void post(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new Post(route, action));
    }

    @Override
    public void websocket(String routeSpecification, WebSocketSessionHandler sessionHandler) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new WebSocket(route, sessionHandler, webSocketOutputFactory));
    }

}
