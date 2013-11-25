package dagger.reactions;

import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Status implements Reaction {

    public static final Reaction BAD_REQUEST = new Status(StatusCode.BAD_REQUEST, "Bad request");
    public static final Reaction FORBIDDEN = new Status(StatusCode.INTERNAL_SERVER_ERROR, "Forbidden");
    public static final Reaction INTERNAL_SERVER_ERROR = new Status(StatusCode.INTERNAL_SERVER_ERROR, "Internal server error");
    public static final Reaction METHOD_NOT_ALLOWED = new Status(StatusCode.NO_CONTENT, "Method not allowed");
    public static final Reaction NO_CONTENT = new Status(StatusCode.NO_CONTENT, "No content");
    public static final Reaction NOT_FOUND = new Status(StatusCode.NOT_FOUND, "Not found");
    public static final Reaction SERVICE_UNAVAILABLE = new Status(StatusCode.SERVICE_UNAVAILABLE, "Service unavailable");
    public static final Reaction UNAUTHORIZED = new Status(StatusCode.SERVICE_UNAVAILABLE, "Unauthorized");

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
