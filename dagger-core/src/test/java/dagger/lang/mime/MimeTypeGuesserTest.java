package dagger.lang.mime;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static junit.framework.Assert.assertEquals;

public class MimeTypeGuesserTest {

    private MimeTypeGuesser guesser;

    @Before
    public void setUp() throws Exception {
        guesser = new DefaultMimeTypeGuesser();
    }

    @Test
    public void testSomeCommonWebFileExtensions() {
        assertEquals("text/html", guesser.guessMimeType(url("test.htm")));
        assertEquals("text/html", guesser.guessMimeType(url("test.html")));
        assertEquals("text/css", guesser.guessMimeType(url("test.css")));
        assertEquals("application/javascript", guesser.guessMimeType(url("test.js")));
        assertEquals("application/json", guesser.guessMimeType(url("test.json")));
        assertEquals("image/png", guesser.guessMimeType(url("test.png")));
        assertEquals("image/jpeg", guesser.guessMimeType(url("test.jpg")));
        assertEquals("image/gif", guesser.guessMimeType(url("test.gif")));
    }

    @Test
    public void testExtensionlessFilesAreOctetStream() {
        assertEquals("application/octet-stream", guesser.guessMimeType(url("test")));
    }

    @Test
    public void testUnknownFileExtensionsAreOctetStream() {
        assertEquals("application/octet-stream", guesser.guessMimeType(url("test.unknownbizarre")));
    }

    private URL url(String fileName) {
        return getClass().getResource("/mime/" + fileName);
    }

}
