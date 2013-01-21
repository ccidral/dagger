package dagger.handlers;

import dagger.*;

public class DefaultRequestHandlerFactory implements RequestHandlerFactory {

    private final RouteFactory routeFactory;

    public DefaultRequestHandlerFactory(RouteFactory routeFactory) {
        this.routeFactory = routeFactory;
    }

    @Override
    public RequestHandler createGet(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        return new Get(route, action);
    }

    @Override
    public RequestHandler createPut(String routeSpecification, Action action) {
        Route route = routeFactory.create(routeSpecification);
        return new Put(route, action);
    }

}
