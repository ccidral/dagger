package dagger.reactions;

import dagger.Reaction;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.mock.MockResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StaticFileTest {

    private MockResponse response;
    private MimeTypeGuesser mimeTypeGuesser;

    @Before
    public void setUp() throws Exception {
        response = new MockResponse();
        mimeTypeGuesser = mock(MimeTypeGuesser.class);
    }

    @Test
    public void testExistingFile() {
        URL fileUrl = getClass().getResource("/view/static/foo/bar/static-file-test.html");
        when(mimeTypeGuesser.guessMimeType(fileUrl)).thenReturn("text/html");

        Reaction reaction = new StaticFile("foo/bar/static-file-test.html", mimeTypeGuesser);

        reaction.execute(response);
        assertOk("text/html", "<html>lorem ipsum</html>");
    }

    @Test
    public void testFileNotFound() {
        Reaction reaction = new StaticFile("bogus.png", mimeTypeGuesser);
        reaction.execute(response);
        assertNotFound();
    }

    @Test
    public void testDoNotMistakeDirectoryForFile() {
        Reaction reaction = new StaticFile("/", mimeTypeGuesser);
        reaction.execute(response);
        assertNotFound();
    }

    private void assertOk(String contentType, String content) {
        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals(contentType, response.getContentType());
        assertEquals(content, response.getOutputAsString());
        assertTrue("Output stream should be closed", response.isOutputStreamClosed());
    }

    private void assertNotFound() {
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Not found.", response.getOutputAsString());
        assertTrue("Output stream should be closed", response.isOutputStreamClosed());
    }

}
