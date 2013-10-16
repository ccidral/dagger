package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.lang.Strings;
import dagger.reactions.Redirection;

/**
 * Redirects to a new location under the context path.
 *
 * <p>
 *     If you need to redirect to a new location not under the
 *     context path, see {@link Redirect}.
 * </p>
 */
public class Reroute implements Action {

    private final String location;

    public Reroute(String location) {
        if(Strings.isNullOrBlank(location))
            throw new IllegalArgumentException("The parameter 'location' must not be null");
        this.location = Strings.prefixIfNecessary('/', location);
    }

    @Override
    public Reaction execute(Request request) throws Exception {
        String contextPath = Strings.emptyIfNull(request.getContextPath());
        return new Redirection(contextPath + location);
    }

}
