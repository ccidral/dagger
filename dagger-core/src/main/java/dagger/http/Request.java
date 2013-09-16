package dagger.http;

import java.io.InputStream;

public interface Request {

    String getContextPath();

    String getURI();

    String getMethod();

    QueryString getQueryString();

    String getHeader(String name);

    String getCookie(String name);

    InputStream getOutputStream();

}
