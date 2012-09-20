package dagger;

import dagger.handlers.ResourceNotFound;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultModuleTest {

    private Module module;
    private ResourceEqualsTo handler1;
    private ResourceStartsWith handler2;

    @Before
    public void setUp() throws Exception {
        handler1 = new ResourceEqualsTo("/foo/bar");
        handler2 = new ResourceStartsWith("/foo");

        module = new DefaultModule();
        module.add(handler1);
        module.add(handler2);
    }

    @Test
    public void testRequestHandlerForRequest() {
        Request request = new MockRequest("/foo/bar");
        RequestHandler requestHandler = module.getHandlerFor(request);

        assertSame(handler1, requestHandler);
    }

    @Test
    public void testRequestHandlerNotFoundForRequest() {
        Request request = new MockRequest("/bar");
        RequestHandler requestHandler = module.getHandlerFor(request);

        assertNotNull(requestHandler);
        assertEquals(ResourceNotFound.class, requestHandler.getClass());
    }

    @Test
    public void testRequestHandlersAreProcessedInTheSameOrderAsTheyWereAdded() {
        assertSame(handler1, module.getHandlerFor(new MockRequest("/foo/bar")));
        assertSame(handler2, module.getHandlerFor(new MockRequest("/foo/bar/baz")));
    }

    private static class ResourceEqualsTo implements RequestHandler {

        private final String resource;

        public ResourceEqualsTo(String resource) {
            this.resource = resource;
        }

        public boolean canHandle(Request request) {
            return resource.equals(request.getURI());
        }

        public Reaction handle(Request request) {
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
            return request.getURI().startsWith(resource);
        }

        public Reaction handle(Request request) {
            return null;
        }

        @Override
        public String toString() {
            return "resource path starting with "+ resource;
        }

    }

    private static class MockRequest implements Request {

        private final String uri;

        public MockRequest(String uri) {
            this.uri = uri;
        }

        public String getURI() {
            return uri;
        }

        public String getMethod() {
            return null;
        }

    }

}
