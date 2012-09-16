package dagger.results;

import dagger.Reaction;
import dagger.http.StatusCode;
import dagger.reactions.NotFound;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotFoundTest {

    private Reaction notFound;
    private MockResponse response;

    @Before
    public void setUp() throws Exception {
        notFound = new NotFound();
        response = new MockResponse();
    }

    @Test
    public void testStatusCodeIs404() {
        notFound.applyTo(response);
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testWriteNotFoundOnResponseOutputStream() {
        notFound.applyTo(response);
        assertEquals("404 - Not found", response.getWrittenText());
    }

}
