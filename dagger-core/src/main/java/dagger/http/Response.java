package dagger.http;

import java.io.OutputStream;

public interface Response {

    OutputStream getOutputStream();

    void setStatusCode(StatusCode statusCode);

    void setContentType(String contentType);

}
