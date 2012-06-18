package dagger;

import dagger.handlers.ResourceNotFound;

import java.util.ArrayList;
import java.util.List;

public class DefaultRequestHandlers implements RequestHandlers {

    private final List<RequestHandler> requestHandlers = new ArrayList<RequestHandler>();

    public void add(RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
    }

    public RequestHandler getHandlerFor(Request request) {
        for(RequestHandler requestHandler : requestHandlers)
            if(requestHandler.canHandle(request))
                return requestHandler;

        return new ResourceNotFound();
    }

}
