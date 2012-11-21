package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeaderNames;
import dagger.http.StatusCode;
import dagger.mock.MockResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OkTest {

    @Test
    public void testTextWithDefaultContentType() throws Exception {
        Reaction reaction = new Ok("Some text");
        MockResponse response = new MockResponse();
        reaction.execute(response);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("Some text", response.getOutputAsString());
        assertEquals("text/plain", response.getHeader(HttpHeaderNames.CONTENT_TYPE));
    }

    @Test
    public void testTextWithProvidedContentType() throws Exception {
        Reaction reaction = new Ok("{}", "application/json");
        MockResponse response = new MockResponse();
        reaction.execute(response);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("{}", response.getOutputAsString());
        assertEquals("application/json", response.getHeader(HttpHeaderNames.CONTENT_TYPE));
    }

    @Test
    public void testBytes() throws Exception {
        Reaction reaction = new Ok(new byte[] {9, 8, 7}, "image/png");
        MockResponse response = new MockResponse();
        reaction.execute(response);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertThat(response.getOutputAsBytes(), is(new byte[] {9, 8, 7}));
        assertEquals("image/png", response.getHeader(HttpHeaderNames.CONTENT_TYPE));
    }

}
