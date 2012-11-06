package dagger.handlers;

import dagger.*;
import dagger.http.Request;

public class Get implements RequestHandler {

    private final Route route;
    private final Action action;

    public Get(Route route, Action action) {
        this.route = route;
        this.action = action;
    }

    @Override
    public boolean canHandle(Request request) {
        if(!request.getMethod().equals("GET"))
            return false;

        return route.matches(request.getURI());
    }

    @Override
    public Reaction handle(Request request) throws Exception {
        return action.execute(request);
    }

}
