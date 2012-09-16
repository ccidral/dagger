package dagger;

import dagger.http.Request;

public interface DaggerModule {

    void add(RequestHandler requestHandler);

    RequestHandler getHandlerFor(Request request);

}
