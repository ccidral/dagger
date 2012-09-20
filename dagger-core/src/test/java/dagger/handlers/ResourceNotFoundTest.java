package dagger.handlers;

import dagger.Reaction;
import dagger.http.Request;
import dagger.reactions.NotFound;
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
        Reaction reaction = new ResourceNotFound().handle(request);

        assertNotNull(reaction);
        assertEquals(NotFound.class, reaction.getClass());
    }

    private static class MockRequest implements Request {

        public String getURI() {
            return null;
        }

        public String getMethod() {
            return null;
        }

    }
}
