package dagger;

import dagger.http.Request;

public interface RequestHandler {

    boolean canHandle(Request request);

    Reaction handle(Request request) throws Exception;

}
