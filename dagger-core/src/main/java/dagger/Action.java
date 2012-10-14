package dagger;

import dagger.http.Request;

public interface Action {

    Reaction execute(Request request) throws Exception;

}
