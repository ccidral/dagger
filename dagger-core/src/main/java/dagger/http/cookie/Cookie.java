package dagger.http.cookie;

import java.util.Date;

public interface Cookie {

    String getName();

    String getValue();

    boolean isSecure();

    void setSecure(boolean secure);

    boolean isHttpOnly();

    void setHttpOnly(boolean httpOnly);

    String getPath();

    void setPath(String path);

    Integer getMaxAge();

    void setMaxAge(Integer seconds);

    Date getExpires();

    void setExpires(Date date);

    String print();

}
