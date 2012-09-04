package dagger.results;

import dagger.Result;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.IOException;

public class Ok implements Result {

    private final String text;

    public Ok(String text) {
        this.text = text;
    }

    @Override
    public void applyTo(Response response) {
        response.setStatusCode(StatusCode.OK);
        try {
            response.getOutputStream().write(text.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
