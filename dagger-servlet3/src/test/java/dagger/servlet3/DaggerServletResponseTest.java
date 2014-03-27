package dagger.servlet3;

import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class DaggerServletResponseTest {

    private HttpServletResponse httpServletResponse;
    private Response response;

    @Before
    public void setUp() throws Exception {
        httpServletResponse = mock(HttpServletResponse.class);
        response = new DaggerServletResponse(httpServletResponse);
    }

    @Test
    public void test_output_stream_is_the_same_as_the_http_servlet_response_output_stream() throws Exception {
        when(httpServletResponse.getOutputStream()).thenReturn(new MockServletOutputStream());
        assertSame(httpServletResponse.getOutputStream(), response.getOutputStream());
    }

    @Test
    public void test_set_status_code() {
        response.setStatusCode(StatusCode.NOT_MODIFIED);
        verify(httpServletResponse).setStatus(SC_NOT_MODIFIED);
    }

    @Test
    public void test_get_status_code() {
        when(httpServletResponse.getStatus()).thenReturn(SC_INTERNAL_SERVER_ERROR);
        assertEquals(StatusCode.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void test_set_header() {
        response.setHeader("Foo", "Bar");
        verify(httpServletResponse).setHeader("Foo", "Bar");
    }

    @Test
    public void test_set_one_cookie() {
        Cookie daggerCookie = mockCookie("Greeting", "Hello");
        response.addCookie(daggerCookie);
        assertEquivalent(daggerCookie, getServletCookie());
    }

    @Test
    public void test_add_cookie_with_secure_option() {
        Cookie daggerCookie = mockCookie("Greeting", "Hello");
        when(daggerCookie.isSecure()).thenReturn(true);
        response.addCookie(daggerCookie);
        assertEquivalent(daggerCookie, getServletCookie());
    }

    @Test
    public void test_add_cookie_with_http_only_option() {
        Cookie daggerCookie = mockCookie("Greeting", "Hello");
        when(daggerCookie.isHttpOnly()).thenReturn(true);
        response.addCookie(daggerCookie);
        assertEquivalent(daggerCookie, getServletCookie());
    }

    @Test
    public void test_add_cookie_with_path_option() {
        Cookie daggerCookie = mockCookie("Greeting", "Hello");
        when(daggerCookie.getPath()).thenReturn("/foo/bar");
        response.addCookie(daggerCookie);
        assertEquivalent(daggerCookie, getServletCookie());
    }

    @Test
    public void test_add_cookie_with_max_age_option() {
        Cookie daggerCookie = mockCookie("Greeting", "Hello");
        when(daggerCookie.getMaxAge()).thenReturn(837);
        response.addCookie(daggerCookie);
        assertEquivalent(daggerCookie, getServletCookie());
    }

    private void assertEquivalent(Cookie daggerCookie, javax.servlet.http.Cookie servletCookie) {
        Integer expectedMaxAge = daggerCookie.getMaxAge() == null ? new Integer(-1) : daggerCookie.getMaxAge();

        assertEquals("Cookie name", daggerCookie.getName(), servletCookie.getName());
        assertEquals("Cookie value", daggerCookie.getValue(), servletCookie.getValue());
        assertEquals("Is secure?", daggerCookie.isSecure(), servletCookie.getSecure());
        assertEquals("Path", daggerCookie.getPath(), getServletCookie().getPath());
        assertEquals("Max age", expectedMaxAge, new Integer(getServletCookie().getMaxAge()));
    }

    private Cookie mockCookie(String name, String value) {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(name);
        when(cookie.getValue()).thenReturn(value);
        when(cookie.getMaxAge()).thenReturn(null);
        return cookie;
    }

    private javax.servlet.http.Cookie getServletCookie() {
        ArgumentCaptor<javax.servlet.http.Cookie> servletCookieArgument = ArgumentCaptor.forClass(javax.servlet.http.Cookie.class);
        verify(httpServletResponse).addCookie(servletCookieArgument.capture());
        return servletCookieArgument.getValue();
    }

    private class MockServletOutputStream extends ServletOutputStream {
        @Override
        public void write(int b) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException();
        }
    }

}
