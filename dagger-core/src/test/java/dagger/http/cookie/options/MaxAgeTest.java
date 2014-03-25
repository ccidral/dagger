package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaxAgeTest {

    @Test
    public void test_zero_max_age() {
        CookieOption maxAge = MaxAge.zero();
        assertEquals("Max-Age=0", maxAge.getValue());
    }

    @Test
    public void test_max_age_in_seconds() {
        CookieOption maxAge = MaxAge.inSeconds(735);
        assertEquals("Max-Age=735", maxAge.getValue());
    }

}
