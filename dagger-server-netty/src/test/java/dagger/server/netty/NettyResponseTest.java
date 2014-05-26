package dagger.server.netty;

import dagger.http.*;
import dagger.http.cookie.Cookie;
import dagger.lang.time.Clock;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NettyResponseTest {

    private static final Date CURRENT_TIME = timestamp(2012, Calendar.NOVEMBER, 15, 20, 45, 7);

    private MockNettyHttpResponse mockNettyHttpResponse;
    private Response response;

    @Before
    public void setUp() throws Exception {
        mockNettyHttpResponse = new MockNettyHttpResponse();

        Clock mockClock = mock(Clock.class);
        when(mockClock.now()).thenReturn(CURRENT_TIME);

        response = new NettyResponse(mockNettyHttpResponse, mockClock);
    }

    @Test
    public void test_date_header_is_created_in_the_constructor() {
        String expectedDate = Formats.timestamp().format(CURRENT_TIME);
        assertEquals(expectedDate, mockNettyHttpResponse.headers().get(HttpHeader.DATE));
    }

    @Test
    public void test_output_stream_is_not_null() throws IOException {
        assertNotNull(response.getOutputStream());
    }

    @Test
    public void test_write_to_output_stream() throws IOException {
        response.getOutputStream().write("hello world".getBytes());

        assertNotNull(mockNettyHttpResponse.data());
        assertEquals("hello world", mockNettyHttpResponse.getWrittenText());
    }

    @Test
    public void test_set_status_code() {
        response.setStatusCode(StatusCode.OK);

        assertNotNull(mockNettyHttpResponse.getStatus());
        assertEquals(StatusCode.OK.getCode(), mockNettyHttpResponse.getStatus().code());
    }

    @Test
    public void test_get_status_code() {
        response.setStatusCode(StatusCode.NOT_FOUND);
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_set_header() {
        response.setHeader("Fruit", "apple");
        assertEquals("apple", mockNettyHttpResponse.headers().get("Fruit"));
    }

    @Test
    public void test_set_new_cookie() {
        Cookie cookie = mockCookie("Greeting", "Hello");

        response.addCookie(cookie);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(1, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("Greeting=Hello"));
    }

    @Test
    public void test_set_two_different_new_cookies() {
        Cookie greeting = mockCookie("Greeting", "Hello");
        Cookie fruit = mockCookie("Fruit", "Apple");

        response.addCookie(greeting);
        response.addCookie(fruit);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(2, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("Greeting=Hello"));
        assertTrue(cookieHeaders.contains("Fruit=Apple"));
    }

    @Test
    public void test_modify_existing_cookie_by_adding_a_cookie_with_the_same_name() {
        Cookie hello = mockCookie("Greeting", "Hello");
        Cookie ahoy = mockCookie("Greeting", "Ahoy");

        response.addCookie(hello);
        response.addCookie(ahoy);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(1, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("Greeting=Ahoy"));
    }

    @Test
    public void test_modifying_existing_cookie_does_not_affect_other_cookies() {
        Cookie fruit = mockCookie("Fruit", "Apple");
        Cookie hello = mockCookie("Greeting", "Hello");
        Cookie ahoy = mockCookie("Greeting", "Ahoy");

        response.addCookie(hello);
        response.addCookie(fruit);
        response.addCookie(ahoy);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(2, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("Fruit=Apple"));
        assertTrue(cookieHeaders.contains("Greeting=Ahoy"));
    }

    private Cookie mockCookie(String name, String value) {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(name);
        when(cookie.print()).thenReturn(name + "=" + value);
        return cookie;
    }

    private static Date timestamp(int year, int month, int day, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hours, minutes, seconds);
        return calendar.getTime();
    }

}
