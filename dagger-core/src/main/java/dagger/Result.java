package dagger;

import dagger.http.Response;

public interface Result {

    void applyOn(Response response);

}
