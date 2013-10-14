package dagger.servlet3.plugin.websocket.grizzly19;

import dagger.http.HttpMethod;
import dagger.http.QueryStringParser;
import dagger.http.Request;
import dagger.lang.io.Streams;
import dagger.servlet3.lang.ServletUriParser;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RequestFactoryTest {

    private RequestFactory requestFactory;

    @Before
    public void setUp() {
        requestFactory = new DefaultRequestFactory(
            mock(ServletUriParser.class),
            mock(QueryStringParser.class)
        );
    }

    @Test
    public void test_http_method_of_dagger_request_created_from_grizzly_request() throws Throwable {
        com.sun.grizzly.tcp.Request grizzlyTcpRequest = mock(com.sun.grizzly.tcp.Request.class);
        Request request = requestFactory.createFrom(grizzlyTcpRequest, "FOOBAR");
        assertEquals("FOOBAR", request.getMethod());
    }

    @Test
    public void test_http_method_of_websocket_open_request() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        Request request = requestFactory.createWebSocketOpenRequest(httpServletRequest);
        assertEquals(HttpMethod.WEBSOCKET_OPEN, request.getMethod());
    }

    @Test
    public void test_http_method_of_websocket_close_request() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        Request request = requestFactory.createWebSocketCloseRequest(httpServletRequest);
        assertEquals(HttpMethod.WEBSOCKET_CLOSE, request.getMethod());
    }

    @Test
    public void test_http_method_of_websocket_message_request() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        Request request = requestFactory.createWebSocketMessageRequest(httpServletRequest, "Hi there!");
        assertEquals(HttpMethod.WEBSOCKET_MESSAGE, request.getMethod());
    }

    @Test
    public void test_message_of_websocket_message_request() throws Throwable {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        Request request = requestFactory.createWebSocketMessageRequest(httpServletRequest, "Hi there!");
        assertEquals("Hi there!", Streams.toString(request.getInputStream()));
    }

}
