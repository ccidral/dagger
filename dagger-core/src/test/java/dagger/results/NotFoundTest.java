package dagger.results;

import dagger.Result;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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
        notFound.applyOn(response);
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testWriteNotFoundOnResponseOutputStream() {
        notFound.applyOn(response);
        assertEquals("404 - Not found", ((MockResponse)response).getWrittenOutput());
    }

    private class MockResponse implements Response {

        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private StatusCode statusCode;

        public StatusCode getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
        }

        public OutputStream getOutputStream() {
            return outputStream;
        }

        public String getWrittenOutput() {
            return new String(outputStream.toByteArray());
        }

    }

}
