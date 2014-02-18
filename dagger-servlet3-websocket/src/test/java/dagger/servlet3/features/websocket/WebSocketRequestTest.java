package dagger.servlet3.features.websocket;

import dagger.http.Request;
import dagger.servlet3.uri.ServletUri;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebSocketRequestTest {

    private ServletUri servletUri;
    private Request request;
    private InputStream inputStream;

    @Before
    public void setUp() throws Exception {
        servletUri = mock(ServletUri.class);
        inputStream = new ByteArrayInputStream(new byte[]{});
        request = new WebSocketRequest("FOOBAR", inputStream, servletUri);
    }

    @Test
    public void test_get_method() {
        assertEquals("FOOBAR", request.getMethod());
    }

    @Test
    public void test_get_context_path() {
        when(servletUri.getContextPath()).thenReturn("/context_path");
        assertEquals("/context_path", request.getContextPath());
    }

    @Test
    public void test_get_uri() {
        when(servletUri.getResourcePath()).thenReturn("/resource_path");
        assertEquals("/resource_path", request.getURI());
    }

    @Test
    public void test_query_string_is_not_null() {
        assertNotNull(request.getQueryString());
    }

    @Test
    public void test_get_input_stream() {
        assertSame(inputStream, request.getInputStream());
    }

}
