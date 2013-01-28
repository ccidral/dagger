package dagger.reactions;

import dagger.Reaction;
import dagger.http.Cookie;
import dagger.http.Request;
import dagger.http.Response;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class WithCookiesTest {

    @Test
    public void testSetCookiesOntoResponse() throws Exception {
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        Cookie greeting = mock(Cookie.class);
        Cookie fruit = mock(Cookie.class);

        Reaction innerReaction = mock(Reaction.class);
        Reaction reactionWithCookies = new WithCookies(innerReaction, greeting, fruit);

        reactionWithCookies.execute(request, response);

        InOrder inOrder = inOrder(response, innerReaction);
        inOrder.verify(response).setCookie(greeting);
        inOrder.verify(response).setCookie(fruit);
        inOrder.verify(innerReaction).execute(request, response);
    }

}
