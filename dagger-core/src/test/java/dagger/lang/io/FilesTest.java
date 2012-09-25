package dagger.lang.io;

import dagger.reactions.StaticFile;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class FilesTest {

    @Test(expected = IllegalArgumentException.class)
    public void testDoNotAcceptNullUrl() {
        Files.isFile(null);
    }

    @Test
    public void testIsFileFromFileSystem() {
        assertTrue("Is a file", Files.isFile(getClass().getResource("/some-directory/some-file.txt")));
        assertFalse("Is not a file", Files.isFile(getClass().getResource("/some-directory")));
    }

    @Test
    public void testIsFileFromJarFile() throws Exception {
        File jarFile = createJar();
        ClassLoader classLoader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() });
        URL fileInsideJar = classLoader.getResource("directory-inside-jar/file-inside-jar.txt");
        URL directoryInsideJar = classLoader.getResource("directory-inside-jar/");

        assertTrue("Is a file", Files.isFile(fileInsideJar));
        assertFalse("Is not a file", Files.isFile(directoryInsideJar));
    }

    private File createJar() throws IOException {
        JavaArchive archive =
            ShrinkWrap.create(JavaArchive.class, "archive.jar")
                .addClasses(StaticFile.class)
                .addAsDirectory("/directory-inside-jar/")
                .addAsResource(getClass().getResource("/some-directory/some-file.txt"), "/directory-inside-jar/file-inside-jar.txt");

        File jarFile = File.createTempFile("test", ".jar");
        archive.as(ZipExporter.class).exportTo(jarFile, true);
        return jarFile;
    }

}
