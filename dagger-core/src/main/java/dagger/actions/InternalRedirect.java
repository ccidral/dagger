package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.reactions.Redirection;

public class InternalRedirect implements Action {

    private final String uriRelativeToContextPath;

    public InternalRedirect(String uriRelativeToContextPath) {
        this.uriRelativeToContextPath = uriRelativeToContextPath;
    }

    @Override
    public Reaction execute(Request request) throws Exception {
        return new Redirection(request.getContextPath() + uriRelativeToContextPath);
    }

}
