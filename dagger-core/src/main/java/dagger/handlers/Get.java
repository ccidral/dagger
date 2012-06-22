package dagger.handlers;

import dagger.*;
import dagger.http.Request;

public class Get implements RequestHandler {

    private final ResourcePattern resourcePattern;
    private final Action action;

    public Get(ResourcePattern resourcePattern, Action action) {
        this.resourcePattern = resourcePattern;
        this.action = action;
    }

    public boolean canHandle(Request request) {
        if(!request.getMethod().equals("GET"))
            return false;

        return resourcePattern.matches(request.getResource());
    }

    public Result handle(Request request) {
        return action.execute();
    }

}
