package dagger.results;

import dagger.Result;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class NotFound implements Result {

    public void applyOn(Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        writeTo(response, "404 - Not found");
    }

    private void writeTo(Response response, String text) {
        OutputStream outputStream = response.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        try {
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
