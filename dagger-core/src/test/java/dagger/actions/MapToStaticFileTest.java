package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.reactions.ResourceFile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapToStaticFileTest {

    private MimeTypeGuesser mimeTypeGuesser;

    @Before
    public void setUp() throws Exception {
        mimeTypeGuesser = mock(MimeTypeGuesser.class);
    }

    @Test
    public void testMapToSpecificFile() throws Exception {
        Action action = new MapToStaticFile("/hello/file.txt", mimeTypeGuesser);

        Reaction reaction = action.execute(request("/foo/bar.html"));

        assertIsStaticFile(reaction);

        ResourceFile resourceFile = (ResourceFile)reaction;
        assertEquals("/hello/file.txt", resourceFile.getPath());
        assertSame(mimeTypeGuesser, resourceFile.getMimeTypeGuesser());
    }

    @Test
    public void testUseUriToFindStaticFile() throws Exception {
        Action action = new MapToStaticFile(mimeTypeGuesser);

        Reaction reaction = action.execute(request("/foo/bar.html"));

        assertIsStaticFile(reaction);

        ResourceFile resourceFile = (ResourceFile)reaction;
        assertEquals("/foo/bar.html", resourceFile.getPath());
        assertSame(mimeTypeGuesser, resourceFile.getMimeTypeGuesser());
    }

    private static void assertIsStaticFile(Reaction reaction) {
        assertNotNull(reaction);
        assertEquals(ResourceFile.class, reaction.getClass());
    }

    private static Request request(String uri) {
        Request request = mock(Request.class);
        when(request.getURI()).thenReturn(uri);
        return request;
    }

}
