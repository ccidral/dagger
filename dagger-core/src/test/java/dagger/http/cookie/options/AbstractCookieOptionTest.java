package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractCookieOptionTest {

    @Test
    public void test_cookies_are_equal_when_their_values_are_equal() {
        CookieOption a = new AbstractCookieOption() {
            @Override
            public String getValue() {
                return "Foo";
            }
        };
        CookieOption b = new AbstractCookieOption() {
            @Override
            public String getValue() {
                return "Foo";
            }
        };
        assertTrue("They should be equal", a.equals(b));
        assertTrue("They should be equal", b.equals(a));
    }

    @Test
    public void test_cookies_are_not_equal_when_their_values_are_not_equal() {
        CookieOption a = new AbstractCookieOption() {
            @Override
            public String getValue() {
                return "Foo";
            }
        };
        CookieOption b = new AbstractCookieOption() {
            @Override
            public String getValue() {
                return "Bar";
            }
        };
        assertFalse("They should not be equal", a.equals(b));
        assertFalse("They should not be equal", b.equals(a));
    }

    @Test
    public void test_a_cookie_is_not_equal_to_a_non_cookie_object() {
        CookieOption cookie = new AbstractCookieOption() {
            @Override
            public String getValue() {
                return "Foo";
            }
        };
        assertFalse("They should not be equal", cookie.equals(new Object()));
    }

}
