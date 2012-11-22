package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.reactions.StaticFile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapToStaticFileTest {

    @Test
    public void testExecute() throws Exception {
        MimeTypeGuesser mimeTypeGuesser = mock(MimeTypeGuesser.class);
        Action action = new MapToStaticFile(mimeTypeGuesser);

        Request request = mock(Request.class);
        when(request.getURI()).thenReturn("/foo/bar.html");

        Reaction reaction = action.execute(request);

        assertNotNull(reaction);
        assertEquals(StaticFile.class, reaction.getClass());

        StaticFile staticFile = (StaticFile)reaction;
        assertEquals("/foo/bar.html", staticFile.getPath());
        assertSame(mimeTypeGuesser, staticFile.getMimeTypeGuesser());
    }

}
