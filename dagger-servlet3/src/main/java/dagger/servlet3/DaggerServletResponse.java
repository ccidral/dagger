package dagger.servlet3;

import dagger.DaggerRuntimeException;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class DaggerServletResponse implements Response {

    private final HttpServletResponse httpServletResponse;

    public DaggerServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            return httpServletResponse.getOutputStream();
        } catch (IOException e) {
            throw new DaggerRuntimeException(e);
        }
    }

    @Override
    public StatusCode getStatusCode() {
        return StatusCode.get(httpServletResponse.getStatus());
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        httpServletResponse.setStatus(statusCode.getCode());
    }

    @Override
    public void setHeader(String name, String value) {
        httpServletResponse.setHeader(name, value);
    }

    @Override
    public void addCookie(Cookie cookie) {
        javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
        servletCookie.setSecure(cookie.isSecure());
        servletCookie.setHttpOnly(cookie.isHttpOnly());
        servletCookie.setPath(cookie.getPath());

        if(cookie.getMaxAge() != null)
            servletCookie.setMaxAge(cookie.getMaxAge());

        httpServletResponse.addCookie(servletCookie);
    }

}
