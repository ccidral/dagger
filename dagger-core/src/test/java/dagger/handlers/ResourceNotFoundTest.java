package dagger.handlers;

import dagger.http.Request;
import dagger.Result;
import dagger.results.NotFound;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResourceNotFoundTest {

    @Test
    public void testCanHandleAnyRequest() {
        assertTrue(new ResourceNotFound().canHandle(null));
    }

    @Test
    public void testReturnsNotFoundResultWhenHandlingRequest() {
        Request request = new MockRequest();
        Result result = new ResourceNotFound().handle(request);

        assertNotNull(result);
        assertEquals(NotFound.class, result.getClass());
    }

    private static class MockRequest implements Request {

        public String getResource() {
            return null;
        }

        public String getMethod() {
            return null;
        }

    }
}
