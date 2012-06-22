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
        Result expectedResult = new MockResult();
        Action action = new MockAction(expectedResult);
        RequestHandler get = new Get(pattern("/foo"), action);

        Request request = new MockRequest("GET", "/foo");
        Result actualResult = get.handle(request);
        assertSame(expectedResult, actualResult);
    }

    private ResourcePattern pattern(String string) {
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

    private class ResourceEqualsTo implements ResourcePattern {

        private final String string;

        public ResourceEqualsTo(String string) {
            this.string = string;
        }

        public boolean matches(String resource) {
            return resource.equals(string);
        }

    }

    private class MockAction implements Action {

        private final Result result;

        public MockAction() {
            this(null);
        }

        public MockAction(Result result) {
            this.result = result;
        }

        public Result execute() {
            return result;
        }
    }

    private class MockResult implements Result {

        public void applyOn(Response response) {
        }

    }

}
