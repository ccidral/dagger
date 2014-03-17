package dagger.servlet3.features.websocket;

import dagger.http.HttpHeader;
import dagger.http.Request;
import dagger.servlet3.uri.ServletURI;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebSocketRequestTest {

    private ServletURI servletUri;
    private InputStream inputStream;
    private Map<String,List<String>> headers;

    @Before
    public void setUp() throws Exception {
        servletUri = mock(ServletURI.class);
        inputStream = new ByteArrayInputStream(new byte[]{});
        headers = new HashMap<>();
    }

    @Test
    public void test_get_method() {
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertEquals("FOOBAR", request.getMethod());
    }

    @Test
    public void test_get_context_path() {
        when(servletUri.getContextPath()).thenReturn("/context_path");
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertEquals("/context_path", request.getContextPath());
    }

    @Test
    public void test_get_uri() {
        when(servletUri.getResourcePath()).thenReturn("/resource_path");
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertEquals("/resource_path", request.getURI());
    }

    @Test
    public void test_query_string_is_not_null() {
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertNotNull(request.getQueryString());
    }

    @Test
    public void test_get_input_stream() {
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertSame(inputStream, request.getInputStream());
    }

    @Test
    public void test_return_null_when_header_is_not_present() {
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertNull(request.getHeader("Foo"));
    }

    @Test
    public void test_get_header() {
        headers.put("Hello", asList("World"));
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertEquals("World", request.getHeader("Hello"));
    }

    @Test
    public void test_get_first_header_value() {
        headers.put("Greeting", asList("Howdy", "Hello"));
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertEquals("Howdy", request.getHeader("Greeting"));
    }

    @Test
    public void test_return_null_if_header_contains_an_empty_list_of_values() {
        headers.put("Foo", new ArrayList<String>());
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertNull(request.getHeader("Foo"));
    }

    @Test
    public void test_header_names_are_case_insensitive() {
        headers.put("Greeting", asList("Hello"));
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertEquals("Hello", request.getHeader("greeting"));
        assertEquals("Hello", request.getHeader("GREETING"));
    }

    @Test
    public void test_return_null_when_cookie_header_is_not_found() {
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertNull(request.getCookie("Foo"));
    }

    @Test
    public void test_return_null_when_cookie_header_contains_empty_list_of_values() {
        headers.put(HttpHeader.COOKIE, new ArrayList<String>());
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertNull(request.getCookie("Foo"));
    }

    @Test
    public void test_get_cookie() {
        headers.put(HttpHeader.COOKIE, asList("Hello=World"));
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertEquals("World", request.getCookie("Hello"));
    }

    @Test
    public void test_get_two_cookies() {
        headers.put(HttpHeader.COOKIE, asList("Greeting=Howdy; Foo=Bar"));
        Request request = new WebSocketRequest("FOOBAR", inputStream, servletUri, headers);
        assertEquals("Howdy", request.getCookie("Greeting"));
        assertEquals("Bar", request.getCookie("Foo"));
    }

}
