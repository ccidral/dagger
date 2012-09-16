package dagger.reactions;

import dagger.Reaction;
import dagger.http.Response;
import dagger.http.StatusCode;

public class NotFound implements Reaction {

    public void applyTo(Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        response.write("404 - Not found");
    }

}
