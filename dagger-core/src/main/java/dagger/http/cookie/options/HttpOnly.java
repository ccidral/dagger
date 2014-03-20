package dagger.http.cookie.options;

public class HttpOnly extends AbstractCookieOption {

    @Override
    public String getValue() {
        return "HttpOnly";
    }

}
