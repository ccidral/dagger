package dagger.reactions;

import dagger.Reaction;
import dagger.http.StatusCode;
import dagger.mock.MockResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class OkTest {

    @Test
    public void testDefaultContentType() {
        Reaction reaction = new Ok("Some text");
        MockResponse response = new MockResponse();
        reaction.execute(response);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("Some text", response.getOutputAsString());
        assertEquals("text/plain", response.getContentType());
    }

    @Test
    public void testWithProvidedContentType() {
        Reaction reaction = new Ok("{}", "application/json");
        MockResponse response = new MockResponse();
        reaction.execute(response);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("{}", response.getOutputAsString());
        assertEquals("application/json", response.getContentType());
    }

}
