package dagger;

import dagger.handlers.ResourceNotFound;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestHandlersTest {

    private RequestHandlers requestHandlers;
    private ResourceEqualsTo handler1;
    private ResourceStartsWith handler2;

    @Before
    public void setUp() throws Exception {
        handler1 = new ResourceEqualsTo("/foo/bar");
        handler2 = new ResourceStartsWith("/foo");

        requestHandlers = new DefaultRequestHandlers();
        requestHandlers.add(handler1);
        requestHandlers.add(handler2);
    }

    @Test
    public void testRequestHandlerForRequest() {
        Request request = new MockRequest("/foo/bar");
        RequestHandler requestHandler = requestHandlers.getHandlerFor(request);

        assertSame(handler1, requestHandler);
    }

    @Test
    public void testRequestHandlerNotFoundForRequest() {
        Request request = new MockRequest("/bar");
        RequestHandler requestHandler = requestHandlers.getHandlerFor(request);

        assertNotNull(requestHandler);
        assertEquals(ResourceNotFound.class, requestHandler.getClass());
    }

    @Test
    public void testRequestHandlersAreProcessedInTheSameOrderAsTheyWereAdded() {
        assertSame(handler1, requestHandlers.getHandlerFor(new MockRequest("/foo/bar")));
        assertSame(handler2, requestHandlers.getHandlerFor(new MockRequest("/foo/bar/baz")));
    }

    private static class ResourceEqualsTo implements RequestHandler {

        private final String resource;

        public ResourceEqualsTo(String resource) {
            this.resource = resource;
        }

        public boolean canHandle(Request request) {
            return resource.equals(request.getResource());
        }

        public Result handle(Request request) {
            return null;
        }

        @Override
        public String toString() {
            return "resource path equals to "+ resource;
        }
    }

    private static class ResourceStartsWith implements RequestHandler {

        private final String resource;

        public ResourceStartsWith(String resource) {
            this.resource = resource;
        }

        public boolean canHandle(Request request) {
            return request.getResource().startsWith(resource);
        }

        public Result handle(Request request) {
            return null;
        }

        @Override
        public String toString() {
            return "resource path starting with "+ resource;
        }

    }

    private static class MockRequest implements Request {

        private final String resource;

        public MockRequest(String resource) {
            this.resource = resource;
        }

        public String getResource() {
            return resource;
        }

        public String getMethod() {
            return null;
        }

    }

}
