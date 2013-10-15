package dagger.module;

import dagger.*;
import dagger.handlers.*;
import dagger.http.Request;
import dagger.lang.NotImplementedYet;
import dagger.websocket.WebSocketSessionFactory;
import dagger.websocket.WebSocketSessionHandler;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ModuleBuilderTest {

    private ModuleBuilder moduleBuilder;
    private MockModule module;
    private Action action;
    private Route route;
    private WebSocketSessionFactory webSocketSessionFactory;

    @Before
    public void setUp() {
        RouteFactory routeFactory = mock(RouteFactory.class);

        module = new MockModule();
        action = mock(Action.class);
        route = mock(Route.class);
        webSocketSessionFactory = mock(WebSocketSessionFactory.class);
        moduleBuilder = new DefaultModuleBuilder(module, routeFactory, webSocketSessionFactory);

        when(routeFactory.create("/foo")).thenReturn(route);
    }
    
    @Test
    public void testAddGetHandler() {
        moduleBuilder.get("/foo", action);
        assertNotNull("Added handler is not null", module.lastAddedHandler);
        assertEquals("Handler type", Get.class, module.lastAddedHandler.getClass());

        Get get = (Get) module.lastAddedHandler;
        assertSame("Route", route, get.getRoute());
        assertSame("Action", action, get.getAction());
    }

    @Test
    public void testAddPutHandler() {
        moduleBuilder.put("/foo", action);
        assertNotNull("Added handler is not null", module.lastAddedHandler);
        assertEquals("Handler type", Put.class, module.lastAddedHandler.getClass());

        Put put = (Put) module.lastAddedHandler;
        assertSame("Route", route, put.getRoute());
        assertSame("Action", action, put.getAction());
    }

    @Test
    public void testAddPostHandler() {
        moduleBuilder.post("/foo", action);
        assertNotNull("Added handler is not null", module.lastAddedHandler);
        assertEquals("Handler type", Post.class, module.lastAddedHandler.getClass());

        Post post = (Post) module.lastAddedHandler;
        assertSame("Route", route, post.getRoute());
        assertSame("Action", action, post.getAction());
    }

    @Test
    public void testAddWebSocketHandler() {
        WebSocketSessionHandler webSocketSessionHandler = mock(WebSocketSessionHandler.class);
        moduleBuilder.websocket("/foo", webSocketSessionHandler);
        assertNotNull("Added handler is not null", module.lastAddedHandler);
        assertEquals("Handler type", WebSocket.class, module.lastAddedHandler.getClass());

        WebSocket websocket = (WebSocket) module.lastAddedHandler;
        assertSame("Route", route, websocket.getRoute());
        assertSame("WebSocket session handler", webSocketSessionHandler, websocket.getSessionHandler());
        assertSame("WebSocket session factory", webSocketSessionFactory, websocket.getWebSocketSessionFactory());
    }

    private class MockModule implements Module {

        public RequestHandler lastAddedHandler;

        @Override
        public void add(RequestHandler requestHandler) {
            this.lastAddedHandler = requestHandler;
        }

        @Override
        public RequestHandler getHandlerFor(Request request) {
            throw new NotImplementedYet();
        }

    }

}
