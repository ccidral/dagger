package dagger.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.reactions.Redirection;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RerouteTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_null_location_is_illegal() {
        new Reroute(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_blank_location_is_illegal() {
        new Reroute("   ");
    }

    @Test
    public void test_reaction_is_never_null() throws Throwable {
        Action action = new Reroute("/new/route");
        Request request = mock(Request.class);
        Reaction reaction = action.execute(request);
        assertNotNull(reaction);
    }

    @Test
    public void test_reaction_is_a_redirection() throws Throwable {
        Action action = new Reroute("/new/route");
        Request request = mock(Request.class);
        Reaction reaction = action.execute(request);
        assertEquals(Redirection.class, reaction.getClass());
    }

    @Test
    public void test_redirect_to_a_new_location_under_the_context_path() throws Throwable {
        Action action = new Reroute("/new/route");
        Request request = mock(Request.class);
        when(request.getContextPath()).thenReturn("/context_path");
        Reaction reaction = action.execute(request);
        assertEquals("/context_path/new/route", ((Redirection)reaction).getLocation());
    }

    @Test
    public void test_allow_null_context_path() throws Throwable {
        Action action = new Reroute("/new/route");
        Request request = mock(Request.class);
        when(request.getContextPath()).thenReturn(null);
        Reaction reaction = action.execute(request);
        assertEquals("/new/route", ((Redirection)reaction).getLocation());
    }

    @Test
    public void test_prefix_location_with_slash_when_it_is_not_prefixed_by_slash() throws Throwable {
        Action action = new Reroute("new/route");
        Request request = mock(Request.class);
        when(request.getContextPath()).thenReturn("/context_path");
        Reaction reaction = action.execute(request);
        assertEquals("/context_path/new/route", ((Redirection)reaction).getLocation());
    }

}
