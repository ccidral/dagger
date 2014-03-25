package dagger.handlers;

import dagger.Reaction;
import dagger.http.HttpHeader;
import dagger.http.Request;
import dagger.RequestHandler;
import dagger.reactions.Status;

public class ResourceNotFound implements RequestHandler {

    public boolean canHandle(Request request) {
        boolean hasUpgradeHeader = request.getHeader(HttpHeader.UPGRADE) != null;
        return !hasUpgradeHeader;
    }

    public Reaction handle(Request request) {
        return Status.NOT_FOUND;
    }

}
