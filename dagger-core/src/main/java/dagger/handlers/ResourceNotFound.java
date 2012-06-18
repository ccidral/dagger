package dagger.handlers;

import dagger.Request;
import dagger.RequestHandler;
import dagger.Result;
import dagger.results.NotFound;

public class ResourceNotFound implements RequestHandler {

    public boolean canHandle(Request request) {
        return true;
    }

    public Result handle(Request request) {
        return new NotFound();
    }

}
