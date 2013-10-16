package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.reactions.Redirection;

/**
 * Redirects to a new location.
 *
 * <p>
 *     If you need to redirect to a new location under the
 *     context path, see {@link Reroute}.
 * </p>
 */
public class Redirect implements Action {

    private final String location;

    public Redirect(String location) {
        this.location = location;
    }

    @Override
    public Reaction execute(Request request) throws Exception {
        return new Redirection(location);
    }

}
