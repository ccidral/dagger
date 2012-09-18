package dagger.reactions;

import dagger.Reaction;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.mock.MockResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StaticFileTest {

    @Test
    public void testExistingFile() {
        Reaction reaction = new StaticFile("foo/bar/static-file-test.html", "text/html");
        MockResponse response = new MockResponse();

        reaction.execute(response);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
        assertEquals("<html>lorem ipsum</html>", response.getOutputAsString());
    }

    @Test
    public void testFileNotFound() {
        Reaction reaction = new StaticFile("bogus.png", "image/png");
        MockResponse response = new MockResponse();

        reaction.execute(response);

        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Not found.", response.getOutputAsString());
    }

}
