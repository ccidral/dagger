package dagger.handlers;

import dagger.*;
import dagger.http.Request;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetTest {

    @Test
    public void testHandlesGetMethodOnly() {
        RequestHandler get = new Get(route("/foo"), new MockAction());
        assertTrue("Should handle GET", get.canHandle(mockRequest("GET", "/foo")));
        assertFalse("Should not handle POST", get.canHandle(mockRequest("POST", "/foo")));
        assertFalse("Should not handle PUT", get.canHandle(mockRequest("PUT", "/foo")));
        assertFalse("Should not handle DELETE", get.canHandle(mockRequest("DELETE", "/foo")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler get = new Get(route("/foo"), new MockAction());
        assertFalse("Should not handle route /bar", get.canHandle(mockRequest("GET", "/bar")));
    }

    @Test
    public void testHandleRequest() throws Exception {
        Reaction expectedReaction = mock(Reaction.class);
        MockAction action = new MockAction(expectedReaction);
        RequestHandler get = new Get(route("/foo"), action);

        Request request = mockRequest("GET", "/foo");
        Reaction actualReaction = get.handle(request);
        assertSame(request, action.receivedRequest);
        assertSame(expectedReaction, actualReaction);
    }

    private Request mockRequest(String method, String uri) {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getURI()).thenReturn(uri);
        return request;
    }

    private Route route(String uri) {
        return new UriEqualsTo(uri);
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
