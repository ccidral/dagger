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

public class WebSocketTest {

    @Test
    public void testHandlesGetMethodOnly() {
        RequestHandler get = new WebSocket(route("/foo"), new MockAction());
        assertTrue("Should handle GET", get.canHandle(mockRequest("GET", "/foo", "WebSocket")));
        assertFalse("Should not handle POST", get.canHandle(mockRequest("POST", "/foo", "WebSocket")));
        assertFalse("Should not handle PUT", get.canHandle(mockRequest("PUT", "/foo", "WebSocket")));
        assertFalse("Should not handle DELETE", get.canHandle(mockRequest("DELETE", "/foo", "WebSocket")));
    }

    @Test
    public void testHandlesOnlyRequestsWithUpgradeHeader() {
        RequestHandler get = new WebSocket(route("/foo"), new MockAction());
        assertFalse("Should not handle GET without 'Upgrade: WebSocket' header", get.canHandle(mockRequest("GET", "/foo", null)));
        assertFalse("Should not handle GET with 'Upgrade: Bogus' header", get.canHandle(mockRequest("GET", "/foo", "Bogus")));
        assertTrue("Should handle GET with Upgrade 'Upgrade: WebSocket' header", get.canHandle(mockRequest("GET", "/foo", "WebSocket")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler get = new WebSocket(route("/foo"), new MockAction());
        assertFalse("Should not handle route /bar", get.canHandle(mockRequest("GET", "/bar", "WebSocket")));
    }

    @Test
    public void testHandleRequest() throws Exception {
        Reaction expectedReaction = mock(Reaction.class);
        MockAction action = new MockAction(expectedReaction);
        RequestHandler get = new WebSocket(route("/foo"), action);

        Request request = mockRequest("GET", "/foo", "WebSocket");
        Reaction actualReaction = get.handle(request);
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
