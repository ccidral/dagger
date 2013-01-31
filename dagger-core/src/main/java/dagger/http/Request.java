package dagger.http;

public interface Request {

    String getURI();

    String getMethod();

    QueryString getQueryString();

    String getHeader(String name);

    String getCookie(String name);

}
