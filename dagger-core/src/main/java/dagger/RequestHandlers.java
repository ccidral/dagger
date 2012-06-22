package dagger;

import dagger.http.Request;

public interface RequestHandlers {

    void add(RequestHandler requestHandler);

    RequestHandler getHandlerFor(Request request);

}
