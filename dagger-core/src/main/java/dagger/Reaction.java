package dagger;

import dagger.http.Request;
import dagger.http.Response;

public interface Reaction {

    void execute(Request request, Response response) throws Exception;

}
