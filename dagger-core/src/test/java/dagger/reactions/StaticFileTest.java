package dagger.reactions;

import dagger.Reaction;
import dagger.http.StatusCode;
import dagger.lang.DelegateClassLoader;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.mock.MockResponse;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StaticFileTest {

    private static final String FILE_PATH = "/foo/bar/static-file-test.html";
    private static final String RESOURCE_NAME = "/view/static" + FILE_PATH;
    public static final String FILE_CONTENTS = "<html>lorem ipsum</html>";
    public static final String CONTENT_TYPE = "text/html";

    private MockResponse response;
    private MimeTypeGuesser mimeTypeGuesser;

    @Before
    public void setUp() throws Exception {
        response = new MockResponse();
        mimeTypeGuesser = mock(MimeTypeGuesser.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilePathMustStartWithSlash() {
        new StaticFile("relative/file/path/not.allowed", mimeTypeGuesser);
    }

    @Test
    public void testExistingFile() {
        URL fileUrl = getClass().getResource(RESOURCE_NAME);
        when(mimeTypeGuesser.guessMimeType(fileUrl)).thenReturn(CONTENT_TYPE);

        Reaction reaction = new StaticFile(FILE_PATH, mimeTypeGuesser);

        reaction.execute(response);
        assertOk();
    }

    @Test
    public void testExistingFileWithinJar() throws Exception {
        ClassLoader classLoader = createClassLoaderFor(createJar());
        URL fileUrl = classLoader.getResource(RESOURCE_NAME.substring(1));

        when(mimeTypeGuesser.guessMimeType(fileUrl)).thenReturn(CONTENT_TYPE);

        Reaction reaction = createReactionInstanceFrom(classLoader);
        reaction.execute(response);
        assertOk();
    }

    @Test
    public void testFileNotFound() {
        Reaction reaction = new StaticFile("/bogus.png", mimeTypeGuesser);
        reaction.execute(response);
        assertNotFound();
    }

    @Test
    public void testDoNotMistakeDirectoryForFile() {
        Reaction reaction = new StaticFile("/", mimeTypeGuesser);
        reaction.execute(response);
        assertNotFound();
    }

    private void assertOk() {
        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals(CONTENT_TYPE, response.getContentType());
        assertEquals(FILE_CONTENTS, response.getOutputAsString());
        assertTrue("Output stream should be closed", response.isOutputStreamClosed());
    }

    private void assertNotFound() {
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Not found.", response.getOutputAsString());
        assertTrue("Output stream should be closed", response.isOutputStreamClosed());
    }

    private File createJar() throws IOException {
        JavaArchive archive =
            ShrinkWrap.create(JavaArchive.class, "archive.jar")
                .addClasses(StaticFile.class)
                .addAsResource(getClass().getResource(RESOURCE_NAME), RESOURCE_NAME);

        File jarFile = File.createTempFile("test", ".jar");
        archive.as(ZipExporter.class).exportTo(jarFile, true);
        return jarFile;
    }

    private Reaction createReactionInstanceFrom(ClassLoader classLoader) throws Exception {
        Class<?> clazz = classLoader.loadClass(StaticFile.class.getName());
        Constructor<?> constructor = clazz.getConstructor(String.class, MimeTypeGuesser.class);
        return (Reaction) constructor.newInstance(FILE_PATH, mimeTypeGuesser);
    }

    private ClassLoader createClassLoaderFor(File jarFile) throws MalformedURLException {
        DelegateClassLoader parentClassLoader = new DelegateClassLoader();

        parentClassLoader.delegateClassToChildrenClassLoaders(StaticFile.class.getName());
        parentClassLoader.delegateResourceToChildrenClassLoaders(RESOURCE_NAME.substring(1));

        return new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, parentClassLoader);
    }

}
