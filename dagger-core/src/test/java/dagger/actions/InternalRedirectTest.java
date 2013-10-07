package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.reactions.Redirection;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InternalRedirectTest {

    private Action action;
    private Request request;

    @Before
    public void setUp() throws Exception {
        action = new InternalRedirect("/foo/bar");
        request = mock(Request.class);
    }

    @Test
    public void test_do_not_return_null_reaction() throws Throwable {
        Reaction reaction = action.execute(request);
        assertNotNull(reaction);
    }

    @Test
    public void test_reaction_is_a_redirection() throws Throwable {
        Reaction reaction = action.execute(request);
        assertEquals(Redirection.class, reaction.getClass());
    }

    @Test
    public void test_prepend_context_path_to_uri() throws Throwable {
        when(request.getContextPath()).thenReturn("/context");
        Redirection redirection = (Redirection) action.execute(request);
        assertEquals("/context/foo/bar", redirection.getLocation());
    }

}
