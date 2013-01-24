package dagger.http;

import java.io.OutputStream;

public interface Response {

    OutputStream getOutputStream();

    StatusCode getStatusCode();

    void setStatusCode(StatusCode statusCode);

    void setHeader(String name, String value);

    void setCookie(String name, String value);

    void setCookie(String name, String value, CookieOptions options);

}
