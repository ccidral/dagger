package dagger.routes;

import dagger.Route;

public class WildcardRoute implements Route {

    private final String pattern;

    public WildcardRoute(String pattern) {
        this.pattern = pattern.replace("*", "[^/]+");
    }

    @Override
    public boolean matches(String uri) {
        return uri.matches(pattern);
    }

}
