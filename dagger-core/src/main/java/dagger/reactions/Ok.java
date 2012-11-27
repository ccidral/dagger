package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeaderNames;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Ok implements Reaction {

    private final byte[] content;
    private final String contentType;
    private final Map<String, String> headers = new HashMap<>();

    public Ok(String content) {
        this(content, "text/plain");
    }

    public Ok(String content, String contentType) {
        this(content.getBytes(), contentType);
    }

    public Ok(byte[] bytes, String contentType) {
        this.content = bytes;
        this.contentType = contentType;
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        for(String headerName : headers.keySet())
            response.setHeader(headerName, headers.get(headerName));

        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, contentType);

        try {
            response.getOutputStream().write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

}
