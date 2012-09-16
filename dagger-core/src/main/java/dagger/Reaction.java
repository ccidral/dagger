package dagger;

import dagger.http.Response;

public interface Reaction {

    void execute(Response response);

}
