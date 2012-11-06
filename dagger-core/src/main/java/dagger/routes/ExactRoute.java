package dagger.routes;

import dagger.Route;

public class ExactRoute implements Route {

    private final String resourceName;

    public ExactRoute(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public boolean matches(String uri) {
        return this.resourceName.equals(uri);
    }

}
