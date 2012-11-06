package dagger.server.netty;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class DevelopmentServer {

    private static final Logger logger = LoggerFactory.getLogger(DevelopmentServer.class);

    public static void main(String[] args) throws Throwable {
        String originalDirectory = args[0];
        String moduleFactoryClassName = args[1];

        DirectoryWatcher directoryWatcher = new DirectoryWatcher(originalDirectory);
        try {
            while(true) {
                File temporaryDirectory = copyToTemporaryDirectory(originalDirectory);
                ClassLoader classLoader = new DirectoryWithJarsClassLoader(temporaryDirectory);
                runServerUntilSomeJarIsChanged(moduleFactoryClassName, classLoader, directoryWatcher);
                FileUtils.deleteDirectory(temporaryDirectory);
            }
        } finally {
            directoryWatcher.stopWatching();
        }
    }

    private static File copyToTemporaryDirectory(String directory) throws IOException {
        File tempDirectoryCopy = createTempDirectory();
        FileUtils.copyDirectory(new File(directory), tempDirectoryCopy);
        logger.info("Copied {} files from {} to {}", tempDirectoryCopy.list().length, directory, tempDirectoryCopy);
        return tempDirectoryCopy;
    }

    private static void runServerUntilSomeJarIsChanged(String moduleFactoryClassName, ClassLoader classLoader, DirectoryWatcher jarDirectoryWatcher) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
        Class<?> moduleFactoryClass = classLoader.loadClass(moduleFactoryClassName);
        Class<?> moduleFactoryInterface = classLoader.loadClass("dagger.ModuleFactory");
        Class<?> nettyServerClass = classLoader.loadClass("dagger.server.netty.NettyServer");

        Object moduleFactory = moduleFactoryClass.newInstance();
        Object module = moduleFactoryClass.getMethod("create").invoke(moduleFactory);
        Object server = nettyServerClass.getConstructor(moduleFactoryInterface).newInstance(module);

        nettyServerClass.getDeclaredMethod("start").invoke(server);

        try {
            jarDirectoryWatcher.waitForChange();
            logger.info("Reloading server");
        } finally {
            nettyServerClass.getDeclaredMethod("stop").invoke(server);
        }
    }

    public static File createTempDirectory() throws IOException {
        final File tempDir = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!tempDir.delete()) throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
        if(!tempDir.mkdir()) throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());

        return tempDir;
    }

}
