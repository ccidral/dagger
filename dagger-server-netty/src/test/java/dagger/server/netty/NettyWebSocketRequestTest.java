package dagger.server.netty;

import dagger.http.Request;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NettyWebSocketRequestTest {

    private Request request;
    private FullHttpRequest httpRequest;

    @Before
    public void setUp() {
        ByteBuf data = Unpooled.copiedBuffer("Foo Bar", Charset.forName("utf-8"));
        TextWebSocketFrame frame = new TextWebSocketFrame(data);

        httpRequest = mock(FullHttpRequest.class);
        when(httpRequest.getUri()).thenReturn("/foo?fruit=apple");
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(httpRequest.headers()).thenReturn(new DefaultHttpHeaders());

        request = new NettyWebSocketRequest(frame, httpRequest);
    }

    @Test
    public void testMethodIsAlwaysGet() {
        assertEquals("Request method", "GET", request.getMethod());
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

}
