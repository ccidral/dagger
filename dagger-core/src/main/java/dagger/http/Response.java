package dagger.http;

import dagger.http.cookie.Cookie;

import java.io.OutputStream;

public interface Response {

    OutputStream getOutputStream();

    StatusCode getStatusCode();

    void setStatusCode(StatusCode statusCode);

    void setHeader(String name, String value);

    void setCookie(Cookie cookie);

}
