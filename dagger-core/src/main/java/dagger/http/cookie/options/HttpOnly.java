package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;

public class HttpOnly implements CookieOption {

    @Override
    public String getValue() {
        return "HttpOnly";
    }

}
