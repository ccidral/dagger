package dagger.routes;

import dagger.Route;
import dagger.RouteFactory;

public class WildcardRouteFactory implements RouteFactory {

    @Override
    public Route create(String specification) {
        return new WildcardRoute(specification);
    }

}
