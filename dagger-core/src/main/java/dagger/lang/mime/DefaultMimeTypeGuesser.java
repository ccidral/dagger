package dagger.lang.mime;

import javax.activation.MimetypesFileTypeMap;
import java.net.URL;

public class DefaultMimeTypeGuesser implements MimeTypeGuesser {

    @Override
    public String guessMimeType(URL url) {
        String fileName = url.getFile();
        return MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(fileName);
    }

}
