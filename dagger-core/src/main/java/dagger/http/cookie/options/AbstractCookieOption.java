package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;

public abstract class AbstractCookieOption implements CookieOption {

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof CookieOption))
            return false;

        CookieOption anotherCookie = (CookieOption)obj;
        return getValue().equals(anotherCookie.getValue());
    }

    @Override
    public String toString() {
        return getClass().getName() + ":" + getValue();
    }

}
