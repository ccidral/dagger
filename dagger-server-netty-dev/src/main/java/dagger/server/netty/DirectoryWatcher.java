package dagger.server.netty;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DirectoryWatcher {

    private final File directory;
    private long directoryTimestamp;
    private final Map<String, Long> fileTimestamps;
    private final Object lock = new Object();
    private WatcherThread watcherThread;

    public DirectoryWatcher(String path) throws Throwable {
        directory = new File(path);
        fileTimestamps = new HashMap<String, Long>();
    }

    public void waitForChange() throws InterruptedException {
        startWatcherThread();
        synchronized (lock) {
            lock.wait();
        }
    }

    private void startWatcherThread() {
        watcherThread = new WatcherThread();
        watcherThread.start();
    }

    private class WatcherThread extends Thread {
        @Override
        public void run() {
            updateTimestamps();
            while(!hasDirectoryChanged()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    private void updateTimestamps() {
        directoryTimestamp = directory.lastModified();
        updateFileTimestamps(directory.listFiles());
    }

    private void updateFileTimestamps(File[] files) {
        fileTimestamps.clear();
        for(File file : files)
            fileTimestamps.put(file.getName(), file.lastModified());
    }

    private boolean hasDirectoryChanged() {
        return
            hasDirectoryTimestampChanged() ||
            hasAnyFileChanged(directory.listFiles());
    }

    private boolean hasDirectoryTimestampChanged() {
        return directoryTimestamp != directory.lastModified();
    }

    private boolean hasAnyFileChanged(File[] files) {
        if(files.length != fileTimestamps.size())
            return true;

        for(File file : files) {
            long previousTimestamp = fileTimestamps.get(file.getName());
            if(previousTimestamp != file.lastModified())
                return true;
        }

        return false;
    }

}
