package dagger.handlers;

import dagger.Action;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.Request;

public class Post implements RequestHandler {

    private final Route route;
    private final Action action;

    public Post(Route route, Action action) {
        this.route = route;
        this.action = action;
    }

    @Override
    public boolean canHandle(Request request) {
        if(!request.getMethod().equals("POST"))
            return false;

        return route.matches(request.getURI());
    }

    @Override
    public Reaction handle(Request request) throws Exception {
        return action.execute(request);
    }

}
