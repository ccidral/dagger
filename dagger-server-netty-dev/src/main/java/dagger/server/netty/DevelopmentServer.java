package dagger.server.netty;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static dagger.server.netty.FileSystem.copyFilesToRandomTemporaryDirectory;
import static dagger.server.netty.SoundPlayer.playSound;

public class DevelopmentServer {

    private final String applicationJarsDirectory;
    private final String moduleFactoryClassName;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Throwable {
        String applicationJarsDirectory = args[0];
        String moduleFactoryClassName = args[1];

        DevelopmentServer developmentServer = new DevelopmentServer(applicationJarsDirectory, moduleFactoryClassName);
        developmentServer.run();
    }

    public DevelopmentServer(String applicationJarsDirectory, String moduleFactoryClassName) {
        this.applicationJarsDirectory = applicationJarsDirectory;
        this.moduleFactoryClassName = moduleFactoryClassName;
    }

    public void run() throws Throwable {
        DirectoryWatcher directoryWatcher = new DirectoryWatcher(applicationJarsDirectory);
        try {
            while(true) {
                File copyOfApplicationJarsDirectory = copyFilesToRandomTemporaryDirectory(applicationJarsDirectory);
                ClassLoader classLoader = new JarDirectoryClassLoader(copyOfApplicationJarsDirectory);
                runWebServerUntilSomeJarIsChanged(classLoader, directoryWatcher);
                FileUtils.deleteDirectory(copyOfApplicationJarsDirectory);
                logger.info("Reloading server");
                playSound("beep-single");
            }
        } finally {
            directoryWatcher.stopWatching();
        }
    }

    private void runWebServerUntilSomeJarIsChanged(ClassLoader classLoader, DirectoryWatcher jarDirectoryWatcher) throws Exception {
        WebServer webServer = new WebServer(moduleFactoryClassName, classLoader);

        webServer.start();
        playSound("beep-double");

        try {
            jarDirectoryWatcher.waitForChange();
        } finally {
            webServer.stop();
        }
    }

}
