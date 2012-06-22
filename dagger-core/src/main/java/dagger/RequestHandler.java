package dagger;

import dagger.http.Request;

public interface RequestHandler {

    boolean canHandle(Request request);

    Result handle(Request request);

}
