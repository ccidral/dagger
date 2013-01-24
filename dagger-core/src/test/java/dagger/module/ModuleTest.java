package dagger.module;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.handlers.ResourceNotFound;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        Request request = mockRequest("/foo/bar");
        RequestHandler requestHandler = module.getHandlerFor(request);

        assertSame(handler1, requestHandler);
    }

    @Test
    public void testRequestHandlerNotFoundForRequest() {
        Request request = mockRequest("/bar");
        RequestHandler requestHandler = module.getHandlerFor(request);

        assertNotNull(requestHandler);
        assertEquals(ResourceNotFound.class, requestHandler.getClass());
    }

    @Test
    public void testRequestHandlersAreProcessedInTheSameOrderAsTheyWereAdded() {
        assertSame(handler1, module.getHandlerFor(mockRequest("/foo/bar")));
        assertSame(handler2, module.getHandlerFor(mockRequest("/foo/bar/baz")));
    }

    private Request mockRequest(String uri) {
        Request request = mock(Request.class);
        when(request.getURI()).thenReturn(uri);
        return request;
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

}
