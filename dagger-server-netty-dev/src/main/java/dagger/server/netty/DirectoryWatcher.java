package dagger.server.netty;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DirectoryWatcher {

    private final int watchId;
    private final Object monitor = new Object();

    private long lastChangeTime;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DirectoryWatcher(String directory) throws Throwable {
        watchId = JNotify.addWatch(directory, JNotify.FILE_ANY, true, new Listener());
    }

    public void stopWatching() throws Throwable {
        JNotify.removeWatch(watchId);
    }

    public void waitForChange() throws InterruptedException {
        lastChangeTime = 0;
        do {
            long timeToWait = timeToWait();

            logger.debug("Will wait " + (timeToWait < Long.MAX_VALUE ? timeToWait + " millis" : "a lot"));

            if(timeToWait > 0)
                synchronized (monitor) {
                    monitor.wait(timeToWait);
                }
        } while(changedLessThanOneSecondAgo());

        logger.info("Age is " + age());
    }

    private long timeToWait() {
        return lastChangeTime == 0 ? Long.MAX_VALUE : 1000L - age();
    }

    private long age() {
        return System.currentTimeMillis() - lastChangeTime;
    }

    private boolean changedLessThanOneSecondAgo() {
        return age() > 0 && age() < 1000L;
    }

    private void markAsChanged(File file) {
        lastChangeTime = System.currentTimeMillis();
        logger.debug("Changed {}", file);
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    private class Listener implements JNotifyListener {

        public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
            markAsChanged(new File(rootPath, oldName));
        }

        public void fileModified(int wd, String rootPath, String name) {
            markAsChanged(new File(rootPath, name));
        }

        public void fileDeleted(int wd, String rootPath, String name) {
            markAsChanged(new File(rootPath, name));
        }

        public void fileCreated(int wd, String rootPath, String name) {
            markAsChanged(new File(rootPath, name));
        }

    }

}
