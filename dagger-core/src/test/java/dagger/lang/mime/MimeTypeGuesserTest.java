package dagger.lang.mime;

import dagger.mime.MimeType;
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
        assertEquals(MimeType.TEXT_HTML, guesser.guessMimeType(url("test.htm")));
        assertEquals(MimeType.TEXT_HTML, guesser.guessMimeType(url("test.html")));
        assertEquals(MimeType.TEXT_CSS, guesser.guessMimeType(url("test.css")));
        assertEquals(MimeType.APPLICATION_JAVASCRIPT, guesser.guessMimeType(url("test.js")));
        assertEquals(MimeType.APPLICATION_JSON, guesser.guessMimeType(url("test.json")));
        assertEquals(MimeType.IMAGE_PNG, guesser.guessMimeType(url("test.png")));
        assertEquals(MimeType.IMAGE_JPEG, guesser.guessMimeType(url("test.jpg")));
        assertEquals(MimeType.IMAGE_GIF, guesser.guessMimeType(url("test.gif")));
    }

    @Test
    public void testExtensionlessFilesAreOctetStream() {
        assertEquals(MimeType.APPLICATION_OCTET_STREAM, guesser.guessMimeType(url("test")));
    }

    @Test
    public void testUnknownFileExtensionsAreOctetStream() {
        assertEquals(MimeType.APPLICATION_OCTET_STREAM, guesser.guessMimeType(url("test.unknownbizarre")));
    }

    private URL url(String fileName) {
        return getClass().getResource("/mime/" + fileName);
    }

}
