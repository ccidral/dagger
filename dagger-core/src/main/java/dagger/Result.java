package dagger;

import dagger.http.Response;

public interface Result {

    void applyTo(Response response);

}
