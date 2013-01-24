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

public class PostTest {

    @Test
    public void testHandlesPostMethodOnly() {
        RequestHandler post = new Post(route("/foo"), new MockAction());
        assertTrue("Should handle POST", post.canHandle(mockRequest("POST", "/foo")));
        assertFalse("Should not handle GET", post.canHandle(mockRequest("GET", "/foo")));
        assertFalse("Should not handle PUT", post.canHandle(mockRequest("PUT", "/foo")));
        assertFalse("Should not handle DELETE", post.canHandle(mockRequest("DELETE", "/foo")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler post = new Post(route("/foo"), new MockAction());
        assertFalse("Should not handle route /bar", post.canHandle(mockRequest("POST", "/bar")));
    }

    @Test
    public void testHandleRequest() throws Exception {
        Reaction expectedReaction = mock(Reaction.class);
        MockAction action = new MockAction(expectedReaction);
        RequestHandler post = new Post(route("/foo"), action);

        Request request = mockRequest("POST", "/foo");
        Reaction actualReaction = post.handle(request);
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
