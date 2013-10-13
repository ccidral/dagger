package dagger.server.netty;

import java.io.File;

import static dagger.server.netty.Millis.seconds;
import static dagger.server.netty.Millis.timeElapsedBetween;

public class DirectoryWatcher {

    private final File directory;
    private final Object lock = new Object();
    private WatcherThread watcherThread;

    public DirectoryWatcher(String path) throws Throwable {
        directory = new File(path);
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
        public WatcherThread() {
            super(WatcherThread.class.getSimpleName());
        }

        @Override
        public void run() {
            long lastTimeSomethingChanged = latestTimestamp();
            do {
                waitFor(seconds(2));
            } while(timeElapsedBetween(lastTimeSomethingChanged, latestTimestamp()) < seconds(3));
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    private void waitFor(long timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) {
        }
    }

    private long latestTimestamp() {
        long lastModified = directory.lastModified();
        for(File file : directory.listFiles())
            if(file.lastModified() > lastModified)
                lastModified = file.lastModified();
        return lastModified;
    }

}
