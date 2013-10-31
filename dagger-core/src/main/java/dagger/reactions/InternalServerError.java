package dagger.reactions;

import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;

@Deprecated
public class InternalServerError implements Reaction {

    @Override
    public void execute(Request request, Response response) throws Exception {
        response.setStatusCode(StatusCode.INTERNAL_SERVER_ERROR);
        response.getOutputStream().write("Internal server error".getBytes());
    }

}
