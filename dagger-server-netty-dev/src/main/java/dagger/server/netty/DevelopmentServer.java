package dagger.server.netty;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

    private static void runServerUntilSomeJarIsChanged(String moduleFactoryClassName, ClassLoader classLoader, DirectoryWatcher jarDirectoryWatcher) throws Exception {
        Class<?> moduleFactoryClass = classLoader.loadClass(moduleFactoryClassName);
        Class<?> moduleInterface = classLoader.loadClass("dagger.Module");
        Class<?> nettyServerClass = classLoader.loadClass("dagger.server.netty.NettyServer");

        Object moduleFactory = moduleFactoryClass.newInstance();
        Object module = moduleFactoryClass.getMethod("create").invoke(moduleFactory);
        Object server = nettyServerClass.getConstructor(moduleInterface).newInstance(module);

        nettyServerClass.getDeclaredMethod("start").invoke(server);
        playSound("beep-double");

        try {
            jarDirectoryWatcher.waitForChange();
            logger.info("Reloading server");
            playSound("beep-single");
        } finally {
            nettyServerClass.getDeclaredMethod("stop").invoke(server);
        }
    }

    private static void playSound(String name) throws Exception {
        Clip clip = AudioSystem.getClip();
        InputStream soundFile = DevelopmentServer.class.getResourceAsStream("/" + name + ".wav");
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        clip.open(ais);
        clip.start();
    }

    public static File createTempDirectory() throws IOException {
        final File tempDir = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!tempDir.delete()) throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
        if(!tempDir.mkdir()) throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());

        return tempDir;
    }

}
