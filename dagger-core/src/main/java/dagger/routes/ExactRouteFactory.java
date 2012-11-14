package dagger.routes;

import dagger.Route;
import dagger.RouteFactory;

public class ExactRouteFactory implements RouteFactory {

    public Route create(String specification) {
        return new ExactRoute(specification);
    }

}
