package dagger.reactions;

import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.IOException;

public class NotFound implements Reaction {

    @Override
    public void execute(Request request, Response response) throws Exception {
        response.setStatusCode(StatusCode.NOT_FOUND);
        try {
            response.getOutputStream().write("404 - Not found".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
