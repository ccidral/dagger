package dagger.server.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevelopmentServer {

    private static final Logger logger = LoggerFactory.getLogger(DevelopmentServer.class);

    public static void main(String[] args) throws Throwable {
        String directory = args[0];
        String moduleClassName = args[1];

        DirectoryWatcher directoryWatcher = new DirectoryWatcher(directory);

        try {
            while(true) {
                ClassLoader classLoader = new DirectoryWithJarsClassLoader(directory);
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
            }
        } finally {
            directoryWatcher.stopWatching();
        }

    }

}
