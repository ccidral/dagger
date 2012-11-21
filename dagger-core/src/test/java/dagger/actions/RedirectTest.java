package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.reactions.Redirection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RedirectTest {

    @Test
    public void testExecute() throws Exception {
        Action action = new Redirect("http://foo/bar");

        Reaction reaction = action.execute(null);

        assertNotNull("Reaction is not null", reaction);
        assertEquals("Reaction is a redirection", Redirection.class, reaction.getClass());
        assertEquals("Redirection location", "http://foo/bar", ((Redirection)reaction).getLocation());
    }

}
