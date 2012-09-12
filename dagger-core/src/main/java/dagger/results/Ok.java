package dagger.results;

import dagger.Result;
import dagger.http.Response;
import dagger.http.StatusCode;

public class Ok implements Result {

    private final String text;

    public Ok(String text) {
        this.text = text;
    }

    @Override
    public void applyTo(Response response) {
        response.setStatusCode(StatusCode.OK);
        response.write(text);
    }

}
