package dagger;

import dagger.http.Response;

public interface Reaction {

    void applyTo(Response response);

}
