package dagger.servlet3;

import dagger.http.QueryString;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DaggerServletRequestTest {

    private HttpServletRequest httpServletRequest;
    private Request request;

    @Before
    public void setUp() throws Exception {
        httpServletRequest = mock(HttpServletRequest.class);
        request = new DaggerServletRequest(httpServletRequest);
    }

    @Test
    public void test_get_context_path() {
        when(httpServletRequest.getContextPath()).thenReturn("/myapp");
        assertEquals("/myapp", request.getContextPath());
    }

    @Test
    public void test_get_uri_when_context_path_is_null() {
        when(httpServletRequest.getContextPath()).thenReturn(null);
        when(httpServletRequest.getRequestURI()).thenReturn("/foo/bar");
        assertEquals("/foo/bar", request.getURI());
    }

    @Test
    public void test_get_uri_when_context_path_is_empty() {
        when(httpServletRequest.getContextPath()).thenReturn("");
        when(httpServletRequest.getRequestURI()).thenReturn("/foo/bar");
        assertEquals("/foo/bar", request.getURI());
    }

    @Test
    public void test_exclude_context_path_when_getting_the_uri() {
        when(httpServletRequest.getContextPath()).thenReturn("/context_path");
        when(httpServletRequest.getRequestURI()).thenReturn("/context_path/foo/bar");
        assertEquals("/foo/bar", request.getURI());
    }

    @Test
    public void test_get_method() {
        when(httpServletRequest.getMethod()).thenReturn("POST");
        assertEquals("POST", request.getMethod());
    }

    @Test
    public void test_query_string_is_not_null_even_when_servlet_request_contains_null_query_string() {
        when(httpServletRequest.getQueryString()).thenReturn(null);
        QueryString queryString = request.getQueryString();
        assertEquals("Is query string null?", false, queryString == null);
    }

    @Test
    public void test_query_string_is_empty_when_servlet_request_contains_null_query_string() {
        when(httpServletRequest.getQueryString()).thenReturn(null);
        QueryString queryString = request.getQueryString();
        assertEquals("Query string size", 0, queryString.size());
    }

    @Test
    public void test_query_string_size() {
        when(httpServletRequest.getQueryString()).thenReturn("hello=world&foo=bar");
        QueryString queryString = request.getQueryString();
        assertEquals(2, queryString.size());
    }

    @Test
    public void test_get_query_string_parameter() {
        when(httpServletRequest.getQueryString()).thenReturn("hello=world&foo=bar");
        QueryString queryString = request.getQueryString();
        assertEquals("world", queryString.get("hello"));
        assertEquals("bar", queryString.get("foo"));
    }

    @Test
    public void test_get_http_headers() {
        when(httpServletRequest.getHeader("Fruit")).thenReturn("Apple");
        when(httpServletRequest.getHeader("Greeting")).thenReturn("Hello");
        assertEquals("Apple", request.getHeader("Fruit"));
        assertEquals("Hello", request.getHeader("Greeting"));
    }

    @Test
    public void test_get_cookies() {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{
            new Cookie("Car", "Mustang"),
            new Cookie("Sport", "Basketball")
        });
        assertEquals("Mustang", request.getCookie("Car"));
        assertEquals("Basketball", request.getCookie("Sport"));
    }

    @Test
    public void test_body() throws IOException {
        when(httpServletRequest.getInputStream()).thenReturn(new MockServletInputStream());
        assertSame(httpServletRequest.getInputStream(), request.getBody());
    }

    private class MockServletInputStream extends ServletInputStream {
        @Override
        public int read() throws IOException {
            throw new UnsupportedOperationException();
        }
    }

}
