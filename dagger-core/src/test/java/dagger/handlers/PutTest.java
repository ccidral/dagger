package dagger.handlers;

import dagger.Action;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.Request;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PutTest {

    @Test
    public void testHandlesPostMethodOnly() {
        RequestHandler put = new Put(route("/foo"), new MockAction());
        assertTrue("Should handle PUT", put.canHandle(mockRequest("PUT", "/foo")));
        assertFalse("Should not handle GET", put.canHandle(mockRequest("GET", "/foo")));
        assertFalse("Should not handle POST", put.canHandle(mockRequest("POST", "/foo")));
        assertFalse("Should not handle DELETE", put.canHandle(mockRequest("DELETE", "/foo")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler put = new Put(route("/foo"), new MockAction());
        assertFalse("Should not handle route /bar", put.canHandle(mockRequest("PUT", "/bar")));
    }

    @Test
    public void testHandleRequest() throws Exception {
        Reaction expectedReaction = mock(Reaction.class);
        MockAction action = new MockAction(expectedReaction);
        RequestHandler put = new Put(route("/foo"), action);

        Request request = mockRequest("PUT", "/foo");
        Reaction actualReaction = put.handle(request);
        assertSame(request, action.receivedRequest);
        assertSame(expectedReaction, actualReaction);
    }

    private Route route(String uri) {
        return new UriEqualsTo(uri);
    }

    private Request mockRequest(String method, String uri) {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getURI()).thenReturn(uri);
        return request;
    }

    private class UriEqualsTo implements Route {

        private final String uri;

        public UriEqualsTo(String uri) {
            this.uri = uri;
        }

        @Override
        public boolean matches(String uri) {
            return uri.equals(this.uri);
        }

    }

    private class MockAction implements Action {

        private final Reaction reaction;
        public Request receivedRequest;

        public MockAction() {
            this(null);
        }

        public MockAction(Reaction reaction) {
            this.reaction = reaction;
        }

        @Override
        public Reaction execute(Request request) {
            this.receivedRequest = request;
            return reaction;
        }

    }

}
