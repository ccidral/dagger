package dagger.handlers;

import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.Request;
import dagger.mock.MockAction;
import dagger.mock.UriEqualsTo;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebSocketMessageTest {

    @Test
    public void testHandlesWsMessageMethodOnly() {
        RequestHandler handler = new WebSocketMessage(route("/foo"), new MockAction());
        assertTrue("Should handle WSMESSAGE", handler.canHandle(mockRequest(WebSocketMessage.METHOD, "/foo", "WebSocket")));
        assertFalse("Should not handle GET", handler.canHandle(mockRequest("GET", "/foo", "WebSocket")));
        assertFalse("Should not handle POST", handler.canHandle(mockRequest("POST", "/foo", "WebSocket")));
        assertFalse("Should not handle PUT", handler.canHandle(mockRequest("PUT", "/foo", "WebSocket")));
        assertFalse("Should not handle DELETE", handler.canHandle(mockRequest("DELETE", "/foo", "WebSocket")));
    }

    @Test
    public void testHandlesOnlyRequestsWithUpgradeHeader() {
        RequestHandler handler = new WebSocketMessage(route("/foo"), new MockAction());
        assertFalse("Should not handle WSMESSAGE without 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSMESSAGE", "/foo", null)));
        assertFalse("Should not handle WSMESSAGE with 'Upgrade: Bogus' header", handler.canHandle(mockRequest("WSMESSAGE", "/foo", "Bogus")));
        assertTrue("Should handle WSMESSAGE with Upgrade 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSMESSAGE", "/foo", "WebSocket")));
    }

    @Test
    public void testIgnoreCaseWhenComparingTheUpgradeHeader() {
        RequestHandler handler = new WebSocketMessage(route("/foo"), new MockAction());
        assertTrue("Should handle WSMESSAGE with 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSMESSAGE", "/foo", "WebSocket")));
        assertTrue("Should handle WSMESSAGE with 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSMESSAGE", "/foo", "websocket")));
        assertTrue("Should handle WSMESSAGE with 'Upgrade: WebSocket' header", handler.canHandle(mockRequest("WSMESSAGE", "/foo", "WEBSOCKET")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler handler = new WebSocketMessage(route("/foo"), new MockAction());
        assertFalse("Should not handle route /bar", handler.canHandle(mockRequest(WebSocketMessage.METHOD, "/bar", "WebSocket")));
    }

    @Test
    public void testHandleRequest() throws Exception {
        Reaction expectedReaction = mock(Reaction.class);
        MockAction action = new MockAction(expectedReaction);
        RequestHandler handler = new WebSocketMessage(route("/foo"), action);

        Request request = mockRequest(WebSocketMessage.METHOD, "/foo", "WebSocket");
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
