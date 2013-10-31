package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeader;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;

public class Redirection implements Reaction {

    private final String location;

    public Redirection(String location) {
        this.location = location;
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        response.setStatusCode(StatusCode.SEE_OTHER);
        response.setHeader(HttpHeader.LOCATION, location);
    }

    public String getLocation() {
        return location;
    }

}
