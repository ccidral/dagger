package dagger.http;

import java.io.OutputStream;

public interface Response {

    StatusCode getStatusCode();

    void setStatusCode(StatusCode statusCode);

    OutputStream getOutputStream();

}
