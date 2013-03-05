package dagger.server.netty;

import dagger.http.*;
import dagger.http.cookie.Cookie;
import dagger.http.cookie.CookieOption;
import dagger.lang.time.Clock;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
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
    public void testDateHeaderIsCreatedInTheConstructor() {
        String expectedDate = Formats.timestamp().format(CURRENT_TIME);
        assertEquals(expectedDate, mockNettyHttpResponse.headers().get(HttpHeaderNames.DATE));
    }

    @Test
    public void testOutputStreamIsNotNull() throws IOException {
        assertNotNull(response.getOutputStream());
    }

    @Test
    public void testWriteToOutputStream() throws IOException {
        response.getOutputStream().write("hello world".getBytes());

        assertNotNull(mockNettyHttpResponse.data());
        assertEquals("hello world", mockNettyHttpResponse.getWrittenText());
    }

    @Test
    public void testSetStatusCode() {
        response.setStatusCode(StatusCode.OK);

        assertNotNull(mockNettyHttpResponse.getStatus());
        assertEquals(StatusCode.OK.getNumber(), mockNettyHttpResponse.getStatus().code());
    }

    @Test
    public void testGetStatusCode() {
        response.setStatusCode(StatusCode.NOT_FOUND);
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSetHeader() {
        response.setHeader("Fruit", "apple");
        assertEquals("apple", mockNettyHttpResponse.headers().get("Fruit"));
    }

    @Test
    public void testSetNewCookie() {
        Cookie cookie = mockCookie("Greeting", "Hello");

        response.setCookie(cookie);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(1, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("Greeting=Hello"));
    }

    @Test
    public void testSetCookieWithOptions() {
        Cookie cookie = mockCookie("Greeting", "Hello", "Option1", "Option2");

        response.setCookie(cookie);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(1, cookieHeaders.size());
        assertEquals("Greeting=Hello; Option1; Option2", cookieHeaders.get(0));
    }

    @Test
    public void testSetTwoDifferentNewCookies() {
        Cookie greeting = mockCookie("Greeting", "Hello");
        Cookie fruit = mockCookie("Fruit", "Apple");

        response.setCookie(greeting);
        response.setCookie(fruit);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(2, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("Greeting=Hello"));
        assertTrue(cookieHeaders.contains("Fruit=Apple"));
    }

    @Test
    public void testSetExistingCookie() {
        Cookie hello = mockCookie("Greeting", "Hello");
        Cookie ahoy = mockCookie("Greeting", "Ahoy");

        response.setCookie(hello);
        response.setCookie(ahoy);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(1, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("Greeting=Ahoy"));
    }

    @Test
    public void testSettingExistingCookieDoesNotAffectOtherCookies() {
        Cookie fruit = mockCookie("Fruit", "Apple");
        Cookie hello = mockCookie("Greeting", "Hello");
        Cookie ahoy = mockCookie("Greeting", "Ahoy");

        response.setCookie(hello);
        response.setCookie(fruit);
        response.setCookie(ahoy);

        List<String> cookieHeaders = mockNettyHttpResponse.headers().getAll("Set-Cookie");
        assertEquals(2, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("Fruit=Apple"));
        assertTrue(cookieHeaders.contains("Greeting=Ahoy"));
    }

    @Test
    public void testCookieNameCannotContainTheEqualsSign() {
        try {
            response.setCookie(mockCookie("hello=", "world"));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testCookieValueCannotBeNull() {
        try {
            response.setCookie(mockCookie("hello", null));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testCookieValueCannotContainCommas() {
        try {
            response.setCookie(mockCookie("hello", "wor,ld"));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testCookieValueCannotContainSemicolons() {
        try {
            response.setCookie(mockCookie("hello", "wor;ld"));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testCookieValueCannotContainWhitespaces() {
        try {
            response.setCookie(mockCookie("hello", "wor ld"));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    private Cookie mockCookie(String name, String value, String... options) {
        Cookie hello = mock(Cookie.class);
        List<CookieOption> cookieOptions = mockCookieOptions(options);

        when(hello.getName()).thenReturn(name);
        when(hello.getValue()).thenReturn(value);
        when(hello.getOptions()).thenReturn(cookieOptions);

        return hello;
    }

    private List<CookieOption> mockCookieOptions(String[] optionValues) {
        List<CookieOption> options = new ArrayList<>();
        for(String value : optionValues) {
            CookieOption option = mock(CookieOption.class);
            when(option.getValue()).thenReturn(value);
            options.add(option);
        }
        return options;
    }

    private static Date timestamp(int year, int month, int day, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hours, minutes, seconds);
        return calendar.getTime();
    }

}
