package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeaderNames;
import dagger.http.StatusCode;
import dagger.mock.MockResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class RedirectionTest {

    public static final String ANOTHER_LOCATION = "http://localhost/another/place.html";

    @Test
    public void testExecute() throws Exception {
        Reaction reaction = new Redirection(ANOTHER_LOCATION);
        MockResponse response = new MockResponse();

        reaction.execute(response);

        assertEquals("HTTP status code", StatusCode.SEE_OTHER, response.getStatusCode());
        assertEquals("HTTP header 'Location'", ANOTHER_LOCATION, response.getHeader(HttpHeaderNames.LOCATION));
    }

    @Test
    public void testGetLocation() {
        Redirection redirection = new Redirection(ANOTHER_LOCATION);
        assertEquals(ANOTHER_LOCATION, redirection.getLocation());
    }

}
