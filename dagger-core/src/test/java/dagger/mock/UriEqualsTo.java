package dagger.mock;

import dagger.Route;

public class UriEqualsTo implements Route {

    private final String uri;

    public UriEqualsTo(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean matches(String uri) {
        return uri.equals(this.uri);
    }

}
