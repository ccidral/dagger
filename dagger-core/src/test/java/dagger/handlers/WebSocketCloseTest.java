package dagger.handlers;

import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.Request;
import dagger.mock.MockAction;
import dagger.mock.UriEqualsTo;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebSocketCloseTest {

    @Test
    public void testHandlesWsCloseMethodOnly() {
        RequestHandler handler = new WebSocketClose(route("/foo"), new MockAction());
        assertTrue("Should handle WSCLOSE", handler.canHandle(mockRequest(WebSocketClose.METHOD, "/foo", "WebSocket")));
        assertFalse("Should not handle GET", handler.canHandle(mockRequest("GET", "/foo", "WebSocket")));
        assertFalse("Should not handle POST", handler.canHandle(mockRequest("POST", "/foo", "WebSocket")));
        assertFalse("Should not handle PUT", handler.canHandle(mockRequest("PUT", "/foo", "WebSocket")));
        assertFalse("Should not handle DELETE", handler.canHandle(mockRequest("DELETE", "/foo", "WebSocket")));
    }

    @Test
    public void testHandlesOnlyRequestsWithUpgradeHeader() {
        RequestHandler handler = new WebSocketClose(route("/foo"), new MockAction());
        assertFalse("Should not handle WSCLOSE without 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSCLOSE", "/foo", null)));
        assertFalse("Should not handle WSCLOSE with 'Upgrade: Bogus' header", handler.canHandle(mockRequest("WSCLOSE", "/foo", "Bogus")));
        assertTrue("Should handle WSCLOSE with Upgrade 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSCLOSE", "/foo", "WebSocket")));
    }

    @Test
    public void testIgnoreCaseWhenComparingTheUpgradeHeader() {
        RequestHandler handler = new WebSocketClose(route("/foo"), new MockAction());
        assertTrue("Should handle WSCLOSE with 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSCLOSE", "/foo", "WebSocket")));
        assertTrue("Should handle WSCLOSE with 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSCLOSE", "/foo", "websocket")));
        assertTrue("Should handle WSCLOSE with 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSCLOSE", "/foo", "WEBSOCKET")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler handler = new WebSocketClose(route("/foo"), new MockAction());
        assertFalse("Should not handle route /bar", handler.canHandle(mockRequest(WebSocketClose.METHOD, "/bar", "WebSocket")));
    }

    @Test
    public void testHandleRequest() throws Exception {
        Reaction expectedReaction = mock(Reaction.class);
        MockAction action = new MockAction(expectedReaction);
        RequestHandler handler = new WebSocketClose(route("/foo"), action);

        Request request = mockRequest(WebSocketClose.METHOD, "/foo", "WebSocket");
        Reaction actualReaction = handler.handle(request);
        assertSame(request, action.receivedRequest);
        assertSame(expectedReaction, actualReaction);
    }

    private Request mockRequest(String method, String uri, String upgradeHeader) {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getURI()).thenReturn(uri);

        if(upgradeHeader != null)
            when(request.getHeader("Upgrade")).thenReturn(upgradeHeader);

        return request;
    }

    private Route route(String uri) {
        return new UriEqualsTo(uri);
    }

}
