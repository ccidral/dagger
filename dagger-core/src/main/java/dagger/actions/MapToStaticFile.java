package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.reactions.ResourceFile;

public class MapToStaticFile implements Action {

    private final String staticFilePath;
    private final MimeTypeGuesser mimeTypeGuesser;

    public MapToStaticFile(String staticFilePath, MimeTypeGuesser mimeTypeGuesser) {
        this.staticFilePath = staticFilePath;
        this.mimeTypeGuesser = mimeTypeGuesser;
    }

    public MapToStaticFile(MimeTypeGuesser mimeTypeGuesser) {
        this(null, mimeTypeGuesser);
    }

    @Override
    public Reaction execute(Request request) throws Exception {
        String uri = this.staticFilePath == null ? request.getURI() : this.staticFilePath;
        return new ResourceFile(uri, mimeTypeGuesser);
    }

}
