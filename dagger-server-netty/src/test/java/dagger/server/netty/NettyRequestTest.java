package dagger.server.netty;

import dagger.http.QueryString;
import dagger.http.Request;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
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
    public void testContextPathIsAlwaysEmptyStringWhichMeansThereIsNoContextPath() {
        Request request = new NettyRequest(mockHttpRequest("/foo/bar"));
        assertEquals("", request.getContextPath());
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
        HttpHeaders headers = new MockHeaders();
        headers.set("Movie", "Star Wars");
        headers.set("Color", "Black");

        FullHttpRequest mockHttpRequest = mock(FullHttpRequest.class);
        when(mockHttpRequest.headers()).thenReturn(headers);

        Request request = new NettyRequest(mockHttpRequest);
        assertEquals("Star Wars", request.getHeader("Movie"));
        assertEquals("Black", request.getHeader("Color"));
    }

    @Test
    public void testGettingUnexistentCookieReturnsNull() {
        HttpHeaders headers = new MockHeaders();
        FullHttpRequest mockHttpRequest = mock(FullHttpRequest.class);
        when(mockHttpRequest.headers()).thenReturn(headers);

        Request request = new NettyRequest(mockHttpRequest);
        assertNull(request.getCookie("bogus"));
    }

    @Test
    public void testGetCookie() {
        HttpHeaders headers = new MockHeaders();
        headers.set("Cookie", "foo=bar; hello=world");

        FullHttpRequest mockHttpRequest = mock(FullHttpRequest.class);
        when(mockHttpRequest.headers()).thenReturn(headers);

        Request request = new NettyRequest(mockHttpRequest);
        assertEquals("bar", request.getCookie("foo"));
        assertEquals("world", request.getCookie("hello"));
    }

    @Test
    public void testGetCookieWithEmptyValue() {
        HttpHeaders headers = new MockHeaders();
        headers.set("Cookie", "foo=bar; empty-cookie=; hello=world");

        FullHttpRequest mockHttpRequest = mock(FullHttpRequest.class);
        when(mockHttpRequest.headers()).thenReturn(headers);

        Request request = new NettyRequest(mockHttpRequest);
        assertNull(request.getCookie("empty-cookie"));
    }

    @Test
    public void testGetCookieWithValueContainingTheEqualSign() {
        HttpHeaders headers = new MockHeaders();
        headers.set("Cookie", "foo=i_have_the=sign; hello=i_dont_have_it");

        FullHttpRequest mockHttpRequest = mock(FullHttpRequest.class);
        when(mockHttpRequest.headers()).thenReturn(headers);

        Request request = new NettyRequest(mockHttpRequest);
        assertEquals("i_have_the=sign", request.getCookie("foo"));
        assertEquals("i_dont_have_it", request.getCookie("hello"));
    }

    @Test
    public void testBody() throws IOException {
        ByteBuf requestContent = Unpooled.copiedBuffer("Hello world".getBytes());

        FullHttpRequest mockHttpRequest = mock(FullHttpRequest.class);
        when(mockHttpRequest.content()).thenReturn(requestContent);

        Request request = new NettyRequest(mockHttpRequest);

        InputStream inputStream = request.getBody();
        String bodyString = IOUtils.toString(inputStream);
        assertEquals("Hello world", bodyString);
    }

    private FullHttpRequest mockHttpRequest(String uri) {
        FullHttpRequest mockHttpRequest = mock(FullHttpRequest.class);
        when(mockHttpRequest.getUri()).thenReturn(uri);
        return mockHttpRequest;
    }

    private FullHttpRequest mockHttpRequest(HttpMethod method) {
        FullHttpRequest mockHttpRequest = mock(FullHttpRequest.class);
        when(mockHttpRequest.getMethod()).thenReturn(method);
        return mockHttpRequest;
    }

}
