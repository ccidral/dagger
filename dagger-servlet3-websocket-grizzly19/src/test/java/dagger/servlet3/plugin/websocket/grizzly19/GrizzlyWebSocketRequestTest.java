package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.util.buf.MessageBytes;
import com.sun.grizzly.util.http.Cookies;
import com.sun.grizzly.util.http.MimeHeaders;
import dagger.http.HttpMethod;
import dagger.http.QueryString;
import dagger.http.QueryStringParser;
import dagger.http.Request;
import dagger.lang.io.Streams;
import dagger.servlet3.lang.ServletUri;
import dagger.servlet3.lang.ServletUriParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GrizzlyWebSocketRequestTest {

    private com.sun.grizzly.tcp.Request grizzlyTcpRequest;
    private ServletUriParser servletUriParser;
    private Request request;
    private QueryStringParser queryStringParser;

    @Before
    public void setUp() throws Exception {
        grizzlyTcpRequest = mock(com.sun.grizzly.tcp.Request.class);
        servletUriParser = mock(ServletUriParser.class);
        queryStringParser = mock(QueryStringParser.class);
        request = new GrizzlyWebSocketRequest(grizzlyTcpRequest, HttpMethod.POST, servletUriParser, queryStringParser);
    }

    private void given_that_grizzly_request_has_uri(String uri) {
        when(grizzlyTcpRequest.requestURI()).thenReturn(messageBytes(uri));
    }

    private void given_that_uri_is_parsed_as_servlet_uri(String uri, ServletUri servletUri) {
        when(servletUriParser.parse(uri)).thenReturn(servletUri);
    }

    private void given_that_grizzly_request_has_query_string(String text) {
        when(grizzlyTcpRequest.queryString()).thenReturn(messageBytes(text));
    }

    private QueryString given_that_query_string_is_parsed(String text) {
        QueryString expectedQueryString = mock(QueryString.class);
        when(queryStringParser.parse(text)).thenReturn(expectedQueryString);
        return expectedQueryString;
    }

    private ServletUri servletUri(String contextPath, String resourcePath) {
        ServletUri servletUri = mock(ServletUri.class);
        when(servletUri.getContextPath()).thenReturn(contextPath);
        when(servletUri.getResourcePath()).thenReturn(resourcePath);
        return servletUri;
    }

    private MessageBytes messageBytes(String text) {
        MessageBytes messageBytes = MessageBytes.newInstance();
        messageBytes.setString(text);
        return messageBytes;
    }

    @Test
    public void test_context_path() {
        given_that_uri_is_parsed_as_servlet_uri("/foo/bar", servletUri("/foo", "/bar"));
        given_that_grizzly_request_has_uri("/foo/bar");
        assertEquals("/foo", request.getContextPath());
    }

    @Test
    public void test_uri() {
        given_that_uri_is_parsed_as_servlet_uri("/foo/bar", servletUri("/foo", "/bar"));
        given_that_grizzly_request_has_uri("/foo/bar");
        assertEquals("/bar", request.getURI());
    }

    @Test
    public void test_arbitrary_http_method() {
        assertEquals(HttpMethod.POST, request.getMethod());
    }

    @Test
    public void test_query_string_is_not_null_when_grizzly_request_has_null_query_string() {
        assertNotNull(request.getQueryString());
    }

    @Test
    public void test_query_string_is_not_null_when_grizzly_request_has_query_string_containing_null_as_value() {
        given_that_grizzly_request_has_query_string(null);
        assertNotNull(request.getQueryString());
    }

    @Test
    public void test_query_string() {
        given_that_grizzly_request_has_query_string("foo=bar&hello=world");
        QueryString queryString = given_that_query_string_is_parsed("foo=bar&hello=world");
        assertSame(queryString, request.getQueryString());
    }

    @Test
    public void test_get_header() {
        when(grizzlyTcpRequest.getHeader("foo")).thenReturn("bar");
        when(grizzlyTcpRequest.getHeader("hello")).thenReturn("world");
        assertEquals("bar", request.getHeader("foo"));
        assertEquals("world", request.getHeader("hello"));
    }

    @Test
    public void test_input_stream_is_not_null() throws IOException {
        assertNotNull(request.getInputStream());
    }

    @Test
    public void test_input_stream_is_always_empty_because_websocket_requests_do_not_have_body() throws IOException {
        assertEquals("", Streams.toString(request.getInputStream()));
    }

    @Test
    public void test_get_cookie() {
        MimeHeaders cookieHeaders = new MimeHeaders();
        cookieHeaders.addValue("Cookie").setString("greeting=hello");
        cookieHeaders.addValue("Cookie").setString("foo=bar");

        when(grizzlyTcpRequest.getCookies()).thenReturn(new Cookies(cookieHeaders));

        assertEquals("hello", request.getCookie("greeting"));
        assertEquals("bar", request.getCookie("foo"));
    }

}
