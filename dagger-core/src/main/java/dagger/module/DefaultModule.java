package dagger.module;

import dagger.Module;
import dagger.RequestHandler;
import dagger.handlers.ResourceNotFound;
import dagger.http.Request;

import java.util.ArrayList;
import java.util.List;

public class DefaultModule implements Module {

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
