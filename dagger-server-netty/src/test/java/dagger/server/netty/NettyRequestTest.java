package dagger.server.netty;

import dagger.http.Request;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NettyRequestTest {

    @Test
    public void testMethod() {
        Request request = new NettyRequest(mockHttpRequest(HttpMethod.GET));
        assertEquals("GET", request.getMethod());

        request = new NettyRequest(mockHttpRequest(HttpMethod.POST));
        assertEquals("POST", request.getMethod());
    }

    @Test
    public void testUri() {
        Request request = new NettyRequest(mockHttpRequest("/foo/bar"));
        assertEquals("/foo/bar", request.getURI());
    }

    @Test
    public void testUriDoesNotIncludeQueryString() {
        Request request = new NettyRequest(mockHttpRequest("/hello/world?fruit=apple&car=mustang"));
        assertEquals("/hello/world", request.getURI());
    }

    @Test
    public void testParametersFromQueryString() {
        Request request = new NettyRequest(mockHttpRequest("/hello/world?fruit=apple&car=mustang"));
        Map<String, String> queryParameters = request.getParameters();

        assertNotNull(queryParameters);
        assertEquals("apple", queryParameters.get("fruit"));
        assertEquals("mustang", queryParameters.get("car"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testParametersFromQueryStringAreUnmodifiable() {
        Request request = new NettyRequest(mockHttpRequest("/hello/world?fruit=apple&car=mustang"));
        request.getParameters().put("fruit", "orange");
    }

    @Test
    public void testGetHeader() {
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getHeader("Movie")).thenReturn("Star Wars");
        when(mockHttpRequest.getHeader("Color")).thenReturn("Black");

        Request request = new NettyRequest(mockHttpRequest);
        assertEquals("Star Wars", request.getHeader("Movie"));
        assertEquals("Black", request.getHeader("Color"));
    }

    @Test
    public void testGettingUnexistentCookieReturnsNull() {
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        Request request = new NettyRequest(mockHttpRequest);
        assertNull(request.getCookie("bogus"));
    }

    @Test
    public void testGetCookie() {
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getHeader("Cookie"))
            .thenReturn("foo=bar; hello=world");

        Request request = new NettyRequest(mockHttpRequest);
        assertEquals("bar", request.getCookie("foo"));
        assertEquals("world", request.getCookie("hello"));
    }

    private HttpRequest mockHttpRequest(String uri) {
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getUri()).thenReturn(uri);
        return mockHttpRequest;
    }

    private HttpRequest mockHttpRequest(HttpMethod method) {
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getMethod()).thenReturn(method);
        return mockHttpRequest;
    }

}
