package dagger.server.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static dagger.server.netty.FileSystem.copyFilesToRandomTemporaryDirectory;
import static dagger.server.netty.SoundPlayer.playSound;
import static org.apache.commons.io.FileUtils.deleteDirectory;

public class DevelopmentServer {

    private final String applicationJarsDirectory;
    private final String moduleFactoryClassName;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Throwable {
        String applicationJarsDirectory = args[0];
        String moduleFactoryClassName = args[1];

        new DevelopmentServer(applicationJarsDirectory, moduleFactoryClassName)
            .run();
    }

    public DevelopmentServer(String applicationJarsDirectory, String moduleFactoryClassName) {
        this.applicationJarsDirectory = applicationJarsDirectory;
        this.moduleFactoryClassName = moduleFactoryClassName;
    }

    public void run() throws Throwable {
        DirectoryWatcher jarDirectoryWatcher = new DirectoryWatcher(applicationJarsDirectory);
        while(true) {
            File copyOfApplicationJarsDirectory = copyFilesToRandomTemporaryDirectory(applicationJarsDirectory);
            ClassLoader classLoader = new JarDirectoryClassLoader(copyOfApplicationJarsDirectory);
            runWebServerUntilSomeJarIsChanged(classLoader, jarDirectoryWatcher);
            deleteDirectory(copyOfApplicationJarsDirectory);
            logger.info("Reloading server");
            playSound("beep-single");
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
