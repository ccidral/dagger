package dagger.module;

import dagger.*;
import dagger.handlers.*;

public class DefaultModuleBuilder implements ModuleBuilder {

    private final Module module;
    private final RouteFactory routeFactory;

    public DefaultModuleBuilder(Module module, RouteFactory routeFactory) {
        this.module = module;
        this.routeFactory = routeFactory;
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
    public void wsopen(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new WebSocketOpen(route, action));
    }

    @Override
    public void wsmessage(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new WebSocketMessage(route, action));
    }

    @Override
    public void wsclose(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        module.add(new WebSocketClose(route, action));
    }

}
