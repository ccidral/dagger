package dagger.routes;

import dagger.Route;

public class AnyRoute implements Route {

    @Override
    public boolean matches(String uri) {
        return true;
    }

}
