package dagger.handlers;

import dagger.Action;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.Request;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class PutTest {

    @Test
    public void testHandlesPostMethodOnly() {
        RequestHandler put = new Put(route("/foo"), new MockAction());
        assertTrue("Should handle PUT", put.canHandle(new MockRequest("PUT", "/foo")));
        assertFalse("Should not handle GET", put.canHandle(new MockRequest("GET", "/foo")));
        assertFalse("Should not handle POST", put.canHandle(new MockRequest("POST", "/foo")));
        assertFalse("Should not handle DELETE", put.canHandle(new MockRequest("DELETE", "/foo")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler put = new Put(route("/foo"), new MockAction());
        assertFalse("Should not handle route /bar", put.canHandle(new MockRequest("PUT", "/bar")));
    }

    @Test
    public void testHandleRequest() throws Exception {
        Reaction expectedReaction = mock(Reaction.class);
        MockAction action = new MockAction(expectedReaction);
        RequestHandler put = new Put(route("/foo"), action);

        Request request = new MockRequest("PUT", "/foo");
        Reaction actualReaction = put.handle(request);
        assertSame(request, action.receivedRequest);
        assertSame(expectedReaction, actualReaction);
    }

    private Route route(String uri) {
        return new UriEqualsTo(uri);
    }

    private static class MockRequest implements Request {

        private final String method;
        private final String uri;

        public MockRequest(String method, String uri) {
            this.method = method;
            this.uri = uri;
        }

        @Override
        public String getURI() {
            return uri;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public Map<String, String> getParameters() {
            return null;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public String getCookie(String name) {
            return null;
        }

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
