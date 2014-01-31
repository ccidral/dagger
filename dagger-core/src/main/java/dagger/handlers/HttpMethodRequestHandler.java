package dagger.handlers;

import dagger.Action;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.HttpHeader;
import dagger.http.Request;

public class HttpMethodRequestHandler implements RequestHandler {

    private final String httpMethod;
    private final Route route;
    private final Action action;

    public HttpMethodRequestHandler(String httpMethod, Route route, Action action) {
        this.httpMethod = httpMethod;
        this.route = route;
        this.action = action;
    }

    @Override
    public boolean canHandle(Request request) {
        return
            httpMethod.equalsIgnoreCase(request.getMethod())
            && request.getHeader(HttpHeader.UPGRADE) == null
            && route.matches(request.getURI());
    }

    @Override
    public Reaction handle(Request request) throws Exception {
        return action.execute(request);
    }

}
