package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeaderNames;
import dagger.http.Response;
import dagger.http.StatusCode;

public class Redirect implements Reaction {

    private final String location;

    public Redirect(String location) {
        this.location = location;
    }

    @Override
    public void execute(Response response) throws Exception {
        response.setStatusCode(StatusCode.SEE_OTHER);
        response.setHeader(HttpHeaderNames.LOCATION, location);
    }

}
