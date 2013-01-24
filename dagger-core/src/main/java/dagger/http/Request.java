package dagger.http;

import java.util.Map;

public interface Request {

    String getURI();

    String getMethod();

    Map<String, String> getParameters();

    String getHeader(String name);

    String getCookie(String name);

}
