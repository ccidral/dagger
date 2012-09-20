package dagger;

import dagger.http.Request;

public interface Module {

    void add(RequestHandler requestHandler);

    RequestHandler getHandlerFor(Request request);

}
