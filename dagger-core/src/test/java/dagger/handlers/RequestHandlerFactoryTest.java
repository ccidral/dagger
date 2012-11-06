package dagger.handlers;

import dagger.*;
import dagger.http.Request;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestHandlerFactoryTest {

    @Test
    public void testCreateGet() throws Exception {
        Route route = mock(Route.class);
        when(route.matches("/foo")).thenReturn(true);
        when(route.matches("/foo/bar")).thenReturn(true);

        RouteFactory routeFactory = mock(RouteFactory.class);
        when(routeFactory.create("/foo")).thenReturn(route);

        Action action = mock(Action.class);

        RequestHandlerFactory factory = new DefaultRequestHandlerFactory(routeFactory);
        RequestHandler get = factory.createGet("/foo", action);


        assertNotNull(get);
        assertTrue(get.canHandle(mockRequest("GET", "/foo")));
        assertTrue(get.canHandle(mockRequest("GET", "/foo/bar")));
        assertFalse(get.canHandle(mockRequest("GET", "/hello")));
        assertFalse(get.canHandle(mockRequest("POST", "/foo")));

        Request request = mockRequest("GET", "/foo");
        Reaction expectedReaction = mock(Reaction.class);
        when(action.execute(request)).thenReturn(expectedReaction);

        Reaction reaction = get.handle(request);
        assertSame(expectedReaction, reaction);
    }

    private Request mockRequest(String method, String uri) {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getURI()).thenReturn(uri);
        return request;
    }

}
