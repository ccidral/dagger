package dagger.module;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.handlers.ResourceNotFound;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ModuleTest {

    private Module module;
    private UriEqualsTo handler1;
    private UriStartsWith handler2;

    @Before
    public void setUp() throws Exception {
        handler1 = new UriEqualsTo("/foo/bar");
        handler2 = new UriStartsWith("/foo");

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

    private static class UriEqualsTo implements RequestHandler {

        private final String uri;

        public UriEqualsTo(String uri) {
            this.uri = uri;
        }

        public boolean canHandle(Request request) {
            return uri.equals(request.getURI());
        }

        public Reaction handle(Request request) {
            return null;
        }

        @Override
        public String toString() {
            return "uri equals to "+ uri;
        }
    }

    private static class UriStartsWith implements RequestHandler {

        private final String prefix;

        public UriStartsWith(String prefix) {
            this.prefix = prefix;
        }

        public boolean canHandle(Request request) {
            return request.getURI().startsWith(prefix);
        }

        public Reaction handle(Request request) {
            return null;
        }

        @Override
        public String toString() {
            return "uri starting with "+ prefix;
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

        @Override
        public Map<String, String> getParameters() {
            return null;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

    }

}
