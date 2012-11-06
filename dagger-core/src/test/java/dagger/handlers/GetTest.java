package dagger.handlers;

import dagger.*;
import dagger.http.Request;
import dagger.http.Response;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class GetTest {

    @Test
    public void testHandlesGetMethodOnly() {
        RequestHandler get = new Get(route("/foo"), new MockAction());
        assertTrue("Should handle GET", get.canHandle(new MockRequest("GET", "/foo")));
        assertFalse("Should not handle POST", get.canHandle(new MockRequest("POST", "/foo")));
        assertFalse("Should not handle PUT", get.canHandle(new MockRequest("PUT", "/foo")));
        assertFalse("Should not handle DELETE", get.canHandle(new MockRequest("DELETE", "/foo")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler get = new Get(route("/foo"), new MockAction());
        assertFalse("Should not handle route /bar", get.canHandle(new MockRequest("GET", "/bar")));
    }

    @Test
    public void testHandleRequest() throws Exception {
        Reaction expectedReaction = new MockReaction();
        MockAction action = new MockAction(expectedReaction);
        RequestHandler get = new Get(route("/foo"), action);

        Request request = new MockRequest("GET", "/foo");
        Reaction actualReaction = get.handle(request);
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
        public Map<String, String> getQueryParameters() {
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

    private class MockReaction implements Reaction {

        @Override
        public void execute(Response response) {
        }

    }

}
