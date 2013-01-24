package dagger.handlers;

import dagger.*;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestHandlerFactoryTest {

    private Action action;
    private RequestHandlerFactory requestHandlerFactory;

    @Before
    public void setUp() {
        Route route = mock(Route.class);
        RouteFactory routeFactory = mock(RouteFactory.class);

        when(route.matches("/foo")).thenReturn(true);
        when(route.matches("/foo/bar")).thenReturn(true);
        when(routeFactory.create("/foo")).thenReturn(route);

        action = mock(Action.class);
        requestHandlerFactory = new DefaultRequestHandlerFactory(routeFactory);
    }

    @Test
    public void testCreateGet() throws Exception {
        RequestHandler get = requestHandlerFactory.createGet("/foo", action);

        assertNotNull(get);
        assertTrue(get.canHandle(mockRequest("GET", "/foo")));
        assertTrue(get.canHandle(mockRequest("GET", "/foo/bar")));
        assertFalse(get.canHandle(mockRequest("GET", "/hello")));
        assertFalse(get.canHandle(mockRequest("POST", "/foo")));
        assertFalse(get.canHandle(mockRequest("PUT", "/foo")));
        assertFalse(get.canHandle(mockRequest("DELETE", "/foo")));

        Request request = mockRequest("GET", "/foo");
        Reaction expectedReaction = mock(Reaction.class);
        when(action.execute(request)).thenReturn(expectedReaction);

        Reaction reaction = get.handle(request);
        assertSame(expectedReaction, reaction);
    }

    @Test
    public void testCreatePut() throws Exception {
        RequestHandler put = requestHandlerFactory.createPut("/foo", action);

        assertNotNull(put);
        assertTrue(put.canHandle(mockRequest("PUT", "/foo")));
        assertTrue(put.canHandle(mockRequest("PUT", "/foo/bar")));
        assertFalse(put.canHandle(mockRequest("PUT", "/hello")));
        assertFalse(put.canHandle(mockRequest("POST", "/foo")));
        assertFalse(put.canHandle(mockRequest("GET", "/foo")));
        assertFalse(put.canHandle(mockRequest("DELETE", "/foo")));

        Request request = mockRequest("PUT", "/foo");
        Reaction expectedReaction = mock(Reaction.class);
        when(action.execute(request)).thenReturn(expectedReaction);

        Reaction reaction = put.handle(request);
        assertSame(expectedReaction, reaction);
    }

    @Test
    public void testCreatePost() throws Exception {
        RequestHandler post = requestHandlerFactory.createPost("/foo", action);

        assertNotNull(post);
        assertTrue(post.canHandle(mockRequest("POST", "/foo")));
        assertTrue(post.canHandle(mockRequest("POST", "/foo/bar")));
        assertFalse(post.canHandle(mockRequest("POST", "/hello")));
        assertFalse(post.canHandle(mockRequest("PUT", "/foo")));
        assertFalse(post.canHandle(mockRequest("GET", "/foo")));
        assertFalse(post.canHandle(mockRequest("DELETE", "/foo")));

        Request request = mockRequest("POST", "/foo");
        Reaction expectedReaction = mock(Reaction.class);
        when(action.execute(request)).thenReturn(expectedReaction);

        Reaction reaction = post.handle(request);
        assertSame(expectedReaction, reaction);
    }

    private Request mockRequest(String method, String uri) {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getURI()).thenReturn(uri);
        return request;
    }

}
