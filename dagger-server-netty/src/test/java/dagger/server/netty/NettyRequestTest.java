package dagger.server.netty;

import dagger.http.QueryString;
import dagger.http.Request;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.*;
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
        QueryString queryParameters = request.getQueryString();

        assertNotNull(queryParameters);
        assertEquals("apple", queryParameters.get("fruit"));
        assertEquals("mustang", queryParameters.get("car"));
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

    @Test
    public void testGetCookieWithEmptyValue() {
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getHeader("Cookie"))
                .thenReturn("foo=bar; empty-cookie=; hello=world");

        Request request = new NettyRequest(mockHttpRequest);
        assertNull(request.getCookie("empty-cookie"));
    }

    @Test
    public void testGetCookieWithValueContainingTheEqualSign() {
        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getHeader("Cookie"))
                .thenReturn("foo=i_have_the=sign; hello=i_dont_have_it");

        Request request = new NettyRequest(mockHttpRequest);
        assertEquals("i_have_the=sign", request.getCookie("foo"));
        assertEquals("i_dont_have_it", request.getCookie("hello"));
    }

    @Test
    public void testBody() throws IOException {
        ByteBuf requestContent = Unpooled.copiedBuffer("Hello world".getBytes());

        HttpRequest mockHttpRequest = mock(HttpRequest.class);
        when(mockHttpRequest.getContent()).thenReturn(requestContent);

        Request request = new NettyRequest(mockHttpRequest);

        InputStream inputStream = request.getBody();
        String bodyString = IOUtils.toString(inputStream);
        assertEquals("Hello world", bodyString);
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
