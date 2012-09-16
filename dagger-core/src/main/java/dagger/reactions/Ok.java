package dagger.reactions;

import dagger.Reaction;
import dagger.http.Response;
import dagger.http.StatusCode;

public class Ok implements Reaction {

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
