package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.reactions.StaticFile;

public class ReturnStaticFile implements Action {

    private final MimeTypeGuesser mimeTypeGuesser;

    public ReturnStaticFile(MimeTypeGuesser mimeTypeGuesser) {
        this.mimeTypeGuesser = mimeTypeGuesser;
    }

    @Override
    public Reaction execute(Request request) throws Exception {
        return new StaticFile(request.getURI(), mimeTypeGuesser);
    }

}
