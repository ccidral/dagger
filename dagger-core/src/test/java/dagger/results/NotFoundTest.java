package dagger.results;

import dagger.Result;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotFoundTest {

    private Response response;
    private Result notFound;

    @Before
    public void setUp() throws Exception {
        response = new MockResponse();
        notFound = new NotFound();
    }

    @Test
    public void testStatusCodeIs404() {
        notFound.applyTo(response);
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testWriteNotFoundOnResponseOutputStream() {
        notFound.applyTo(response);
        assertEquals("404 - Not found", ((MockResponse)response).getWrittenOutput());
    }

}
