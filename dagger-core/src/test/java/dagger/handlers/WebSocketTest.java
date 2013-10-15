package dagger.handlers;

import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.UnexpectedHttpMethodException;
import dagger.websocket.WebSocketSession;
import dagger.websocket.WebSocketSessionFactory;
import dagger.websocket.WebSocketSessionHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static dagger.http.HttpMethod.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class WebSocketTest {

    private Route route;
    private WebSocketSessionHandler webSocketSessionHandler;
    private RequestHandler requestHandler;
    private WebSocketSessionFactory webSocketSessionFactory;

    @Before
    public void setUp() throws Exception {
        route = mock(Route.class);
        webSocketSessionHandler = mock(WebSocketSessionHandler.class);
        webSocketSessionFactory = mock(WebSocketSessionFactory.class);
        requestHandler = new WebSocket(route, webSocketSessionHandler, webSocketSessionFactory);

        when(route.matches("/foo/bar")).thenReturn(true);
    }

    private WebSocketSession given_that_websocket_session_will_be_created_for(Response response) {
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        when(webSocketSessionFactory.create(response)).thenReturn(webSocketSession);
        return webSocketSession;
    }

    private Request request(String uri, String method) {
        return request(uri, method, "");
    }

    private Request request(String uri, String method, String body) {
        Request request = mock(Request.class);
        when(request.getURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn(method);
        when(request.getInputStream()).thenReturn(new ByteArrayInputStream(body.getBytes()));
        return request;
    }

    @Test
    public void test_cannot_handle_GET_requests() {
        assertFalse(requestHandler.canHandle(request("/foo/bar", GET)));
    }

    @Test
    public void test_cannot_handle_POST_requests() {
        assertFalse(requestHandler.canHandle(request("/foo/bar", POST)));
    }

    @Test
    public void test_cannot_handle_PUT_requests() {
        assertFalse(requestHandler.canHandle(request("/foo/bar", PUT)));
    }

    @Test
    public void test_cannot_handle_DELETE_requests() {
        assertFalse(requestHandler.canHandle(request("/foo/bar", DELETE)));
    }

    @Test
    public void test_can_handle_WSOPEN_requests() {
        assertTrue(requestHandler.canHandle(request("/foo/bar", WEBSOCKET_OPEN)));
    }

    @Test
    public void test_can_handle_WSCLOSE_requests() {
        assertTrue(requestHandler.canHandle(request("/foo/bar", WEBSOCKET_CLOSE)));
    }

    @Test
    public void test_can_handle_WSMESSAGE_requests() {
        assertTrue(requestHandler.canHandle(request("/foo/bar", WEBSOCKET_MESSAGE)));
    }

    @Test
    public void test_cannot_handle_unexpected_route() {
        assertFalse(requestHandler.canHandle(request("/hello/world", WEBSOCKET_OPEN)));
    }

    @Test(expected = UnexpectedHttpMethodException.class)
    public void test_throw_exception_on_attempt_to_handle_request_with_unexpected_http_method() throws Throwable {
        requestHandler.handle(request("/foo/bar", GET));
    }

    @Test
    public void test_handle_websocket_open_request() throws Throwable {
        Request request = request("/foo/bar", WEBSOCKET_OPEN);
        Response response = mock(Response.class);
        WebSocketSession webSocketSession = given_that_websocket_session_will_be_created_for(response);

        Reaction reaction = requestHandler.handle(request);
        reaction.execute(request, response);

        verify(webSocketSessionHandler).onOpen(request, webSocketSession);
    }

    @Test
    public void test_handle_websocket_message() throws Throwable {
        Request request = request("/foo/bar", WEBSOCKET_MESSAGE, "Hello there");
        Response response = mock(Response.class);
        WebSocketSession webSocketSession = given_that_websocket_session_will_be_created_for(response);

        Reaction reaction = requestHandler.handle(request);
        reaction.execute(request, response);

        verify(webSocketSessionHandler).onMessage(request, webSocketSession, "Hello there");
    }

    @Test
    public void test_handle_websocket_close_request() throws Throwable {
        Request request = request("/foo/bar", WEBSOCKET_CLOSE);
        Response response = mock(Response.class);

        Reaction reaction = requestHandler.handle(request);
        reaction.execute(request, response);

        verify(webSocketSessionHandler).onClose(request);
    }

}
