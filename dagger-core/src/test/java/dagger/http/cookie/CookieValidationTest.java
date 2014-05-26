package dagger.http.cookie;

import org.junit.Test;

public class CookieValidationTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_cookie_name_cannot_contain_the_equals_sign() {
        new CookieImpl("hello=", "world");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_cookie_value_cannot_be_null() {
        new CookieImpl("hello", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_cookie_value_cannot_contain_commas() {
        new CookieImpl("hello", "wor,ld");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_cookie_value_cannot_contain_semicolons() {
        new CookieImpl("hello", "wor;ld");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_cookie_value_cannot_contain_whitespaces() {
        new CookieImpl("hello", "wor ld");
    }

}
