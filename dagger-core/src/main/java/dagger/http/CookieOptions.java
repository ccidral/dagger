package dagger.http;

public interface CookieOptions {

    String getOptionsString();

    void setSecure(boolean secure);

    void setHttpOnly(boolean httpOnly);

}
