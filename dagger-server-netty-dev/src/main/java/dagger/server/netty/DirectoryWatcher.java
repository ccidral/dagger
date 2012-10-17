package dagger.server.netty;

import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher {

    private final WatchKey key;
    private final WatchService watchService;

    public DirectoryWatcher(String directory) throws Throwable {
        Path dir = FileSystems.getDefault().getPath(directory);

        watchService = FileSystems.getDefault().newWatchService();
        key = dir.register(watchService,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY);
    }

    public void stopWatching() throws Throwable {
        watchService.close();
    }

    public void waitForChange() throws InterruptedException {
        while(true) {
            Thread.sleep(1000);

            List<WatchEvent<?>> event = key.pollEvents();

            key.reset();

            if(event.size() > 0)
                break;
        }
    }

}
