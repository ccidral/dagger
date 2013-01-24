package dagger.server.netty;

import dagger.http.*;
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
    private Clock mockClock;

    @Before
    public void setUp() throws Exception {
        mockNettyHttpResponse = new MockNettyHttpResponse();
        mockClock = mock(Clock.class);

        when(mockClock.now()).thenReturn(CURRENT_TIME);

        response = new NettyResponse(mockNettyHttpResponse, mockClock);
    }

    @Test
    public void testDateHeaderIsCreatedInTheConstructor() {
        String expectedDate = Formats.timestamp().format(CURRENT_TIME);
        assertEquals(expectedDate, mockNettyHttpResponse.getHeader(HttpHeaderNames.DATE));
    }

    @Test
    public void testOutputStreamIsNotNull() throws IOException {
        assertNotNull(response.getOutputStream());
    }

    @Test
    public void testWriteToOutputStream() throws IOException {
        response.getOutputStream().write("hello world".getBytes());

        assertNotNull(mockNettyHttpResponse.getContent());
        assertEquals("hello world", mockNettyHttpResponse.getWrittenText());
    }

    @Test
    public void testSetStatusCode() {
        response.setStatusCode(StatusCode.OK);

        assertNotNull(mockNettyHttpResponse.getStatus());
        assertEquals(StatusCode.OK.getNumber(), mockNettyHttpResponse.getStatus().getCode());
    }

    @Test
    public void testGetStatusCode() {
        response.setStatusCode(StatusCode.NOT_FOUND);
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSetHeader() {
        response.setHeader("Fruit", "apple");
        assertEquals("apple", mockNettyHttpResponse.getHeader("Fruit"));
    }

    @Test
    public void testSetNewCookie() {
        response.setCookie("foo", "bar");

        List<String> cookieHeaders = mockNettyHttpResponse.getHeaders("Set-Cookie");
        assertEquals(1, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("foo=bar"));
    }

    @Test
    public void testSetTwoDifferentNewCookies() {
        response.setCookie("foo", "bar");
        response.setCookie("hello", "world");

        List<String> cookieHeaders = mockNettyHttpResponse.getHeaders("Set-Cookie");
        assertEquals(2, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("foo=bar"));
        assertTrue(cookieHeaders.contains("hello=world"));
    }

    @Test
    public void testSetExistingCookie() {
        response.setCookie("foo", "bar");
        response.setCookie("foo", "blurbles");

        List<String> cookieHeaders = mockNettyHttpResponse.getHeaders("Set-Cookie");
        assertEquals(1, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("foo=blurbles"));
    }

    @Test
    public void testSettingExistingCookieDoesNotAffectOtherCookies() {
        response.setCookie("foo", "bar");
        response.setCookie("hello", "world");
        response.setCookie("foo", "blurbles");

        List<String> cookieHeaders = mockNettyHttpResponse.getHeaders("Set-Cookie");
        assertEquals(2, cookieHeaders.size());
        assertTrue(cookieHeaders.contains("foo=blurbles"));
        assertTrue(cookieHeaders.contains("hello=world"));
    }

    @Test
    public void testSetCookieWithOptions() {
        CookieOptions options = mock(CookieOptions.class);
        when(options.getOptionsString()).thenReturn("Secure; HttpOnly");

        response.setCookie("hello", "world", options);

        List<String> cookieHeaders = mockNettyHttpResponse.getHeaders("Set-Cookie");
        assertEquals("hello=world; Secure; HttpOnly", cookieHeaders.get(0));
    }

    @Test
    public void testCookieNameCannotContainTheEqualsSign() {
        try {
            response.setCookie("hello=", "world");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        try {
            response.setCookie("hello=", "world", mock(CookieOptions.class));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testCookieValueCannotContainCommas() {
        try {
            response.setCookie("hello", "wor,ld");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        try {
            response.setCookie("hello", "wor,ld", mock(CookieOptions.class));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testCookieValueCannotContainSemicolons() {
        try {
            response.setCookie("hello", "wor;ld");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        try {
            response.setCookie("hello", "wor;ld", mock(CookieOptions.class));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testCookieValueCannotContainWhitespaces() {
        try {
            response.setCookie("hello", "wor ld");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        try {
            response.setCookie("hello", "wor ld", mock(CookieOptions.class));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    private static Date timestamp(int year, int month, int day, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hours, minutes, seconds);
        return calendar.getTime();
    }

}
