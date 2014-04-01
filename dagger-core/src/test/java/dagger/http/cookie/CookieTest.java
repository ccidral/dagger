package dagger.http.cookie;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CookieTest {

    private Cookie cookie;

    @Before
    public void setUp() throws Exception {
        cookie = new CookieImpl("Greeting", "Hello");
    }

    @Test
    public void test_get_cookie_name() {
        assertEquals("Greeting", cookie.getName());
    }

    @Test
    public void test_get_cookie_value() {
        assertEquals("Hello", cookie.getValue());
    }

    @Test
    public void test_secure_option_is_disabled_by_default() {
        assertEquals("Is secure?", false, cookie.isSecure());
    }

    @Test
    public void test_set_secure_option() {
        cookie.setSecure(true);
        assertEquals("Is secure?", true, cookie.isSecure());
    }

    @Test
    public void test_http_only_option_is_disabled_by_default() {
        assertEquals("Is HTTP only?", false, cookie.isHttpOnly());
    }

    @Test
    public void test_set_http_only_option() {
        cookie.setHttpOnly(true);
        assertEquals("Is HTTP only?", true, cookie.isHttpOnly());
    }

    @Test
    public void test_path_option_is_null_by_default() {
        assertNull(cookie.getPath());
    }

    @Test
    public void test_set_path_option() {
        cookie.setPath("/foo/bar");
        assertEquals("/foo/bar", cookie.getPath());
    }

    @Test
    public void test_max_age_option_is_null_by_default() {
        assertNull(cookie.getMaxAge());
    }

    @Test
    public void test_set_max_age_option() {
        cookie.setMaxAge(new Integer(2112));
        assertEquals(new Integer(2112), cookie.getMaxAge());
    }

    @Test
    public void test_expires_option_is_null_by_default() {
        assertNull(cookie.getExpires());
    }

    @Test
    public void test_set_expires_option() {
        Date date = new Date();
        cookie.setExpires(date);
        assertEquals(date, cookie.getExpires());
    }

    @Test
    public void test_print_cookie() {
        assertEquals("Greeting=Hello", cookie.print());
    }

    @Test
    public void test_print_cookie_with_secure_option() {
        cookie.setSecure(true);
        assertEquals("Greeting=Hello; Secure", cookie.print());
    }

    @Test
    public void test_print_cookie_with_http_only_option() {
        cookie.setHttpOnly(true);
        assertEquals("Greeting=Hello; HttpOnly", cookie.print());
    }

    @Test
    public void test_print_cookie_with_path_option() {
        cookie.setPath("/foo/bar");
        assertEquals("Greeting=Hello; Path=/foo/bar", cookie.print());
    }

    @Test
    public void test_print_cookie_with_max_age_option_set_to_minus_one() {
        cookie.setMaxAge(-1);
        assertEquals("Greeting=Hello; Max-Age=-1", cookie.print());
    }

    @Test
    public void test_print_cookie_with_max_age_option_set_to_zero() {
        cookie.setMaxAge(0);
        assertEquals("Greeting=Hello; Max-Age=0", cookie.print());
    }

    @Test
    public void test_print_cookie_with_max_age_option_greater_than_zero() {
        cookie.setMaxAge(312);
        assertEquals("Greeting=Hello; Max-Age=312", cookie.print());
    }

    @Test
    public void test_print_cookie_with_expires_option_set_to_beginning_of_time() {
        cookie.setExpires(beginningOfJavaTime());
        assertEquals("Greeting=Hello; Expires=Thu, 01-Jan-1970 00:00:00 GMT", cookie.print());
    }

    @Test
    public void test_print_cookie_with_expires_option_set_to_the_last_second_of_the_year() {
        cookie.setExpires(lastSecondOfYear(2013));
        assertEquals("Greeting=Hello; Expires=Tue, 31-Dec-2013 23:59:59 GMT", cookie.print());
    }

    @Test
    public void test_print_cookie_with_all_options_set_to_something() {
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/some/path");
        cookie.setMaxAge(735);
        cookie.setExpires(lastSecondOfYear(2014));
        assertEquals("Greeting=Hello" +
                "; Secure" +
                "; HttpOnly" +
                "; Path=/some/path" +
                "; Max-Age=735" +
                "; Expires=Wed, 31-Dec-2014 23:59:59 GMT", cookie.print());
    }

    private Date beginningOfJavaTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendar.setTimeInMillis(0);
        return calendar.getTime();
    }

    private Date lastSecondOfYear(int year) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23 + (int)offset(calendar.getTimeZone()));
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private long offset(TimeZone timeZone) {
        long daylightSavingTime = hours(timeZone.getDSTSavings());
        long rawOffset = hours(timeZone.getRawOffset());
        return rawOffset + daylightSavingTime;
    }

    private long hours(long millis) {
        return millis / 1000 / 60 / 60;
    }

}
