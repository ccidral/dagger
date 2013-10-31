package dagger.handlers;

import dagger.Reaction;
import dagger.http.Request;
import dagger.RequestHandler;
import dagger.reactions.Status;

public class ResourceNotFound implements RequestHandler {

    public boolean canHandle(Request request) {
        return true;
    }

    public Reaction handle(Request request) {
        return Status.NOT_FOUND;
    }

}
