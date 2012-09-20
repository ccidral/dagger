package dagger.lang.io;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Files {

    public static boolean isFile(URL url) {
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return file.isFile();
    }

}
