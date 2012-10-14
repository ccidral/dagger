package dagger.server.netty;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.IOUtils;

import java.io.File;
import java.io.IOException;

public class DevelopmentServer {

    private static final Logger logger = LoggerFactory.getLogger(DevelopmentServer.class);

    public static void main(String[] args) throws Throwable {
        String directory = args[0];
        String moduleClassName = args[1];

        DirectoryWatcher directoryWatcher = new DirectoryWatcher(directory);

        try {
            while(true) {
                File tempDirectoryCopy = createTempDirectory();
                FileUtils.copyDirectory(new File(directory), tempDirectoryCopy);
                logger.info("Copied to temporary directory: {}", tempDirectoryCopy);

                ClassLoader classLoader = new DirectoryWithJarsClassLoader(tempDirectoryCopy);
                Class<?> moduleClass = classLoader.loadClass(moduleClassName);
                Class<?> moduleInterface = classLoader.loadClass("dagger.Module");
                Class<?> nettyServerClass = classLoader.loadClass("dagger.server.netty.NettyServer");

                Object module = moduleClass.newInstance();
                Object server = nettyServerClass.getConstructor(moduleInterface).newInstance(module);

                nettyServerClass.getDeclaredMethod("start").invoke(server);

                try {
                    directoryWatcher.waitForChange();
                    logger.info("Reloading server");
                } finally {
                    nettyServerClass.getDeclaredMethod("stop").invoke(server);
                }

                FileUtils.deleteDirectory(tempDirectoryCopy);
            }
        } finally {
            directoryWatcher.stopWatching();
        }

    }

    public static File createTempDirectory() throws IOException {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return (temp);
    }

}
