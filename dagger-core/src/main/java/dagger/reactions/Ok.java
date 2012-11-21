package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeaderNames;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.IOException;

public class Ok implements Reaction {

    private final byte[] content;
    private final String contentType;

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
    public void execute(Response response) {
        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, contentType);

        try {
            response.getOutputStream().write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
