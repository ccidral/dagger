package dagger.server.netty;

import dagger.http.Request;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NettyWebSocketRequestTest {

    private Request request;
    private FullHttpRequest httpRequest;

    @Before
    public void setUp() {
        httpRequest = mock(FullHttpRequest.class);
        when(httpRequest.getUri()).thenReturn("/foo?fruit=apple");
        when(httpRequest.headers()).thenReturn(new DefaultHttpHeaders());

        request = new NettyWebSocketRequest("Foo Bar", "HELLO", httpRequest);
    }

    @Test
    public void testContextPathIsAlwaysEmptyStringWhichMeansThereIsNoContextPath() {
        assertEquals("", request.getContextPath());
    }

    @Test
    public void testMethod() {
        assertEquals("Request method", "HELLO", request.getMethod());
    }

    @Test
    public void testQueryString() {
        assertNotNull("Query string", request.getQueryString());
        assertEquals("Query string size", 0, request.getQueryString().size());
    }

    @Test
    public void testBody() throws IOException {
        assertNotNull("Request body", request.getBody());
        assertEquals("Request body", "Foo Bar", IOUtils.toString(request.getBody()));
    }

    @Test
    public void testUri() {
        assertEquals("Request uri", "/foo", request.getURI());
    }

    @Test
    public void testHeaders() {
        httpRequest.headers().add("Foo", "Bar");
        assertEquals("Bar", request.getHeader("Foo"));
    }

    @Test
    public void testCookies() {
        assertNull(request.getCookie("Hello"));
        assertNull(request.getCookie("Foo"));

        httpRequest.headers().set("Cookie", "Hello=World; Foo=Bar");

        assertEquals("World", request.getCookie("Hello"));
        assertEquals("Bar", request.getCookie("Foo"));
    }

}
