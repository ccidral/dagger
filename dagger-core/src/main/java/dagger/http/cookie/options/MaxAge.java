package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;

public class MaxAge extends AbstractCookieOption {

    private int seconds;

    public MaxAge(int seconds) {
        this.seconds = seconds;
    }

    public static CookieOption inSeconds(int seconds) {
        return new MaxAge(seconds);
    }

    public static CookieOption zero() {
        return new MaxAge(0);
    }

    public String getValue() {
        return "Max-Age=" + seconds;
    }

}
