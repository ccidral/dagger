package dagger.http;

import java.io.OutputStream;

public interface Response {

    OutputStream getOutputStream();

    StatusCode getStatusCode();

    void setStatusCode(StatusCode statusCode);

    String getContentType();

    void setContentType(String contentType);

    void setHeader(String name, String value);

}
