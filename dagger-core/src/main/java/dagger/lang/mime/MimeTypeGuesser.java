package dagger.lang.mime;

import java.net.URL;

public interface MimeTypeGuesser {

    String guessMimeType(URL url);

}
