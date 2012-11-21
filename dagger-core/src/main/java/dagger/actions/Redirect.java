package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.reactions.Redirection;

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
