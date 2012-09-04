package dagger.handlers;

import dagger.*;
import dagger.http.Request;

public class Get implements RequestHandler {

    private final ResourceMatcher resourceMatcher;
    private final Action action;

    public Get(ResourceMatcher resourceMatcher, Action action) {
        this.resourceMatcher = resourceMatcher;
        this.action = action;
    }

    public boolean canHandle(Request request) {
        if(!request.getMethod().equals("GET"))
            return false;

        return resourceMatcher.matches(request.getResource());
    }

    public Result handle(Request request) {
        return action.execute();
    }

}
