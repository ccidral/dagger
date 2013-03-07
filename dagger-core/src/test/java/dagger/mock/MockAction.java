package dagger.mock;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;

public class MockAction implements Action {

    private final Reaction reaction;
    public Request receivedRequest;

    public MockAction() {
        this(null);
    }

    public MockAction(Reaction reaction) {
        this.reaction = reaction;
    }

    @Override
    public Reaction execute(Request request) {
        this.receivedRequest = request;
        return reaction;
    }

}
