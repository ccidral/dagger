package dagger.reactions;

import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Status implements Reaction {

    private final StatusCode statusCode;
    private final String message;

    public Status(StatusCode statusCode) {
        this(statusCode, null);
    }

    public Status(StatusCode statusCode, String message) {
        if(statusCode == null) throw new IllegalArgumentException("Parameter 'statusCode' must not be null");
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        response.setStatusCode(statusCode);

        if(message != null)
            writeMessageTo(response);
    }

    private void writeMessageTo(Response response) throws IOException {
        Writer writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(message);
        writer.flush();
    }

}
