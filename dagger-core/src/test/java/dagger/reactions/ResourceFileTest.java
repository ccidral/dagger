package dagger.reactions;

import dagger.Reaction;
import dagger.http.Formats;
import dagger.http.HttpHeader;
import dagger.http.Request;
import dagger.http.StatusCode;
import dagger.lang.DelegateClassLoader;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.mime.MimeType;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static dagger.http.HttpHeader.IF_MODIFIED_SINCE;
import static dagger.http.HttpHeader.LAST_MODIFIED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceFileTest {

    private static final String FILE_PATH = "/foo/bar/static-file-test.html";
    private static final String RESOURCE_NAME = "/view/static" + FILE_PATH;
    public static final String FILE_CONTENTS = "<html>lorem ipsum</html>";

    private Request request;
    private MockResponse response;
    private MimeTypeGuesser mimeTypeGuesser;

    @Before
    public void setUp() throws Exception {
        request = mock(Request.class);
        response = new MockResponse();
        mimeTypeGuesser = mock(MimeTypeGuesser.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilePathMustStartWithSlash() {
        new ResourceFile("relative/file/path/not.allowed", mimeTypeGuesser);
    }

    @Test
    public void testExistingFile() throws Exception {
        URL fileUrl = getClass().getResource(RESOURCE_NAME);
        when(mimeTypeGuesser.guessMimeType(fileUrl)).thenReturn(MimeType.TEXT_HTML);

        Reaction reaction = new ResourceFile(FILE_PATH, mimeTypeGuesser);

        reaction.execute(request, response);
        assertOk();
    }

    @Test
    public void testExistingFileInsideJar() throws Exception {
        ClassLoader classLoader = createClassLoaderFor(createJar());
        URL fileUrl = classLoader.getResource(withoutTrailingSlash(RESOURCE_NAME));

        when(mimeTypeGuesser.guessMimeType(fileUrl)).thenReturn(MimeType.TEXT_HTML);

        Reaction reaction = createReactionInstanceFrom(classLoader);
        reaction.execute(request, response);
        assertOk();
    }

    @Test
    public void testLastModifiedHeader() throws Exception {
        Date modificationDate = getFileModificationDate();
        String expectedLastModifiedValue = Formats.timestamp().format(modificationDate);

        Reaction reaction = new ResourceFile(FILE_PATH, mimeTypeGuesser);
        reaction.execute(request, response);

        assertEquals(expectedLastModifiedValue, response.getHeader("Last-Modified"));
    }

    @Test
    public void testLastModifiedHeaderForFilesInsideJar() throws Exception {
        File jarFile = createJar();
        ZipFile jar = new ZipFile(jarFile);
        ZipEntry fileInsideJar = jar.getEntry(withoutTrailingSlash(RESOURCE_NAME));
        Date fileDateInsideJar = new Date(fileInsideJar.getTime());
        String expectedLastModifiedValue = Formats.timestamp().format(fileDateInsideJar);

        ClassLoader classLoader = createClassLoaderFor(createJar());
        Reaction reaction = createReactionInstanceFrom(classLoader);
        reaction.execute(request, response);

        assertEquals(expectedLastModifiedValue, response.getHeader(LAST_MODIFIED));
    }

    @Test
    public void testFileNotFound() throws Exception {
        Reaction reaction = new ResourceFile("/bogus.png", mimeTypeGuesser);
        reaction.execute(request, response);
        assertNotFound();
    }

    @Test
    public void testDoNotMistakeDirectoryForFile() throws Exception {
        Reaction reaction = new ResourceFile("/", mimeTypeGuesser);
        reaction.execute(request, response);
        assertNotFound();
    }

    @Test
    public void testReturnStatusNotModifiedIfNotModifiedSinceFileModificationDate() throws Exception {
        Date modificationDate = getFileModificationDate();
        Date ifModifiedSince = new Date(modificationDate.getTime() + 1000);

        when(request.getHeader(IF_MODIFIED_SINCE)).thenReturn(Formats.timestamp().format(ifModifiedSince));

        Reaction reaction = new ResourceFile(FILE_PATH, mimeTypeGuesser);
        reaction.execute(request, response);

        assertEquals(StatusCode.NOT_MODIFIED, response.getStatusCode());
    }

    @Test
    public void testReturnStatusNotModifiedIfModificationDateEqualsToIfModifiedSinceHeader() throws Exception {
        Date modificationDate = getFileModificationDate();
        Date ifModifiedSince = new Date(modificationDate.getTime());

        when(request.getHeader(IF_MODIFIED_SINCE)).thenReturn(Formats.timestamp().format(ifModifiedSince));

        Reaction reaction = new ResourceFile(FILE_PATH, mimeTypeGuesser);
        reaction.execute(request, response);

        assertEquals(StatusCode.NOT_MODIFIED, response.getStatusCode());
    }

    @Test
    public void testReturnOkIfModificationDateIsAfterIfModifiedSinceHeader() throws Exception {
        Date modificationDate = getFileModificationDate();
        Date ifModifiedSince = new Date(modificationDate.getTime() - 1000);

        when(request.getHeader(IF_MODIFIED_SINCE)).thenReturn(Formats.timestamp().format(ifModifiedSince));

        Reaction reaction = new ResourceFile(FILE_PATH, mimeTypeGuesser);
        reaction.execute(request, response);

        assertEquals(StatusCode.OK, response.getStatusCode());
    }

    @Test
    public void testIgnoreIncorrectFormatOfIfModifiedSinceHeader() throws Exception {
        when(request.getHeader(IF_MODIFIED_SINCE)).thenReturn("bogus date");

        Reaction reaction = new ResourceFile(FILE_PATH, mimeTypeGuesser);
        reaction.execute(request, response);

        assertEquals(StatusCode.OK, response.getStatusCode());
    }

    private Date getFileModificationDate() throws URISyntaxException {
        File file = new File(getClass().getResource(RESOURCE_NAME).toURI());
        return new Date(file.lastModified());
    }

    private void assertOk() {
        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals(MimeType.TEXT_HTML, response.getHeader(HttpHeader.CONTENT_TYPE));
        assertEquals(FILE_CONTENTS, response.getOutputAsString());
        assertTrue("Output stream should be closed", response.isOutputStreamClosed());
    }

    private void assertNotFound() {
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
        assertEquals(MimeType.TEXT_PLAIN, response.getHeader(HttpHeader.CONTENT_TYPE));
        assertEquals("Not found.", response.getOutputAsString());
        assertTrue("Output stream should be closed", response.isOutputStreamClosed());
    }

    private File createJar() throws IOException {
        JavaArchive archive =
            ShrinkWrap.create(JavaArchive.class, "archive.jar")
                .addClasses(ResourceFile.class)
                .addAsResource(getClass().getResource(RESOURCE_NAME), RESOURCE_NAME);

        File jarFile = File.createTempFile("test", ".jar");
        archive.as(ZipExporter.class).exportTo(jarFile, true);
        return jarFile;
    }

    private Reaction createReactionInstanceFrom(ClassLoader classLoader) throws Exception {
        Class<?> clazz = classLoader.loadClass(ResourceFile.class.getName());
        Constructor<?> constructor = clazz.getConstructor(String.class, MimeTypeGuesser.class);
        return (Reaction) constructor.newInstance(FILE_PATH, mimeTypeGuesser);
    }

    private ClassLoader createClassLoaderFor(File jarFile) throws MalformedURLException {
        DelegateClassLoader parentClassLoader = new DelegateClassLoader();

        parentClassLoader.delegateClassToChildrenClassLoaders(ResourceFile.class.getName());
        parentClassLoader.delegateResourceToChildrenClassLoaders(withoutTrailingSlash(RESOURCE_NAME));

        return new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, parentClassLoader);
    }

    private String withoutTrailingSlash(String resourceName) {
        return RESOURCE_NAME.substring(1);
    }

}
