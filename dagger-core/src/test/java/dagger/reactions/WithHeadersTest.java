package dagger.reactions;

import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class WithHeadersTest {

    private Request request;
    private Response response;
    private Reaction innerReaction;
    private WithHeaders action;

    @Before
    public void setUp() throws Exception {
        request = mock(Request.class);
        response = mock(Response.class);
        innerReaction = mock(Reaction.class);
        action = new WithHeaders(innerReaction);
    }

    @Test
    public void test_execute_inner_reaction() throws Throwable {
        action.execute(request, response);
        verify(innerReaction).execute(request, response);
    }

    @Test
    public void test_no_headers_set() throws Throwable {
        action.execute(request, response);
        verify(response, never()).setHeader(anyString(), anyString());
    }

    @Test
    public void test_set_one_header() throws Throwable {
        action.set("Greeting", "hello");
        action.execute(request, response);
        verify(response).setHeader("Greeting", "hello");
    }

    @Test
    public void test_set_two_headers() throws Throwable {
        action
            .set("Greeting", "hi")
            .set("Car", "mustang");

        action.execute(request, response);

        verify(response).setHeader("Greeting", "hi");
        verify(response).setHeader("Car", "mustang");
    }

    @Test
    public void test_execute_inner_reaction_after_setting_headers() throws Throwable {
        action.set("Foo", "bar");

        action.execute(request, response);

        InOrder inOrder = inOrder(innerReaction, response);
        inOrder.verify(response).setHeader("Foo", "bar");
        inOrder.verify(innerReaction).execute(request, response);
    }

}
