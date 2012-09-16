package dagger.handlers;

import dagger.*;
import dagger.http.Request;
import dagger.http.Response;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetTest {

    @Test
    public void testHandlesGetMethodOnly() {
        RequestHandler get = new Get(pattern("/foo"), new MockAction());
        assertTrue("Should handle GET", get.canHandle(new MockRequest("GET", "/foo")));
        assertFalse("Should not handle POST", get.canHandle(new MockRequest("POST", "/foo")));
        assertFalse("Should not handle PUT", get.canHandle(new MockRequest("PUT", "/foo")));
        assertFalse("Should not handle DELETE", get.canHandle(new MockRequest("DELETE", "/foo")));
    }

    @Test
    public void testDoesNotHandleDifferentResource() {
        RequestHandler get = new Get(pattern("/foo"), new MockAction());
        assertFalse("Should not handle resource /bar", get.canHandle(new MockRequest("GET", "/bar")));
    }

    @Test
    public void testHandleRequest() {
        Reaction expectedReaction = new MockReaction();
        Action action = new MockAction(expectedReaction);
        RequestHandler get = new Get(pattern("/foo"), action);

        Request request = new MockRequest("GET", "/foo");
        Reaction actualReaction = get.handle(request);
        assertSame(expectedReaction, actualReaction);
    }

    private ResourceName pattern(String string) {
        return new ResourceEqualsTo(string);
    }

    private static class MockRequest implements Request {

        private final String method;
        private final String resource;

        public MockRequest(String method, String resource) {
            this.method = method;
            this.resource = resource;
        }

        public String getResource() {
            return resource;
        }

        public String getMethod() {
            return method;
        }

    }

    private class ResourceEqualsTo implements ResourceName {

        private final String string;

        public ResourceEqualsTo(String string) {
            this.string = string;
        }

        public boolean matches(String uri) {
            return uri.equals(string);
        }

    }

    private class MockAction implements Action {

        private final Reaction reaction;

        public MockAction() {
            this(null);
        }

        public MockAction(Reaction reaction) {
            this.reaction = reaction;
        }

        public Reaction execute() {
            return reaction;
        }
    }

    private class MockReaction implements Reaction {

        public void applyTo(Response response) {
        }

    }

}
