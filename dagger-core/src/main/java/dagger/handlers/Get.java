package dagger.handlers;

import dagger.*;
import dagger.http.Request;

public class Get implements RequestHandler {

    private final ResourceName resourceName;
    private final Action action;

    public Get(ResourceName resourceName, Action action) {
        this.resourceName = resourceName;
        this.action = action;
    }

    public boolean canHandle(Request request) {
        if(!request.getMethod().equals("GET"))
            return false;

        return resourceName.matches(request.getResource());
    }

    public Reaction handle(Request request) {
        return action.execute(request);
    }

}
