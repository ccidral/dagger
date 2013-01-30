package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;

public class Secure implements CookieOption {

    @Override
    public String getValue() {
        return "Secure";
    }

}
