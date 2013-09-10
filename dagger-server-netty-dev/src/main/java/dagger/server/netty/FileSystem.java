package dagger.server.netty;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class FileSystem {

    private static final Logger logger = LoggerFactory.getLogger(FileSystem.class);

    public static File createTempDirectory() throws IOException {
        final File tempDir = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!tempDir.delete()) throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
        if(!tempDir.mkdir()) throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());

        return tempDir;
    }

    public static File copyFilesToRandomTemporaryDirectory(String directory) throws IOException {
        File tempDirectoryCopy = createTempDirectory();
        FileUtils.copyDirectory(new File(directory), tempDirectoryCopy);
        logger.info("Copied {} files from {} to {}", tempDirectoryCopy.list().length, directory, tempDirectoryCopy);
        return tempDirectoryCopy;
    }

}
