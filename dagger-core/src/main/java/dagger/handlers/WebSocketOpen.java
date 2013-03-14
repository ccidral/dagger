package dagger.handlers;

import dagger.Action;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.Request;

public class WebSocketOpen implements RequestHandler {

    public static final String METHOD = "WSOPEN";

    private final Route route;
    private final Action action;

    public WebSocketOpen(Route route, Action action) {
        this.route = route;
        this.action = action;
    }

    @Override
    public boolean canHandle(Request request) {
        if(!request.getMethod().equals(METHOD))
            return false;

        if(!"WebSocket".equals(request.getHeader("Upgrade")))
            return false;

        return route.matches(request.getURI());
    }

    @Override
    public Reaction handle(Request request) throws Exception {
        return action.execute(request);
    }

    public Route getRoute() {
        return route;
    }

    public Action getAction() {
        return action;
    }

}