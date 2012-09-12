package dagger.results;

import dagger.Result;
import dagger.http.Response;
import dagger.http.StatusCode;

public class NotFound implements Result {

    public void applyTo(Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        response.write("404 - Not found");
    }

}
