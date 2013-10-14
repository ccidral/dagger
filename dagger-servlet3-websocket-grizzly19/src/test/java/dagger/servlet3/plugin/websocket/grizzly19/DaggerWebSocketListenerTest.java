package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketListener;
import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import dagger.http.Response;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DaggerWebSocketListenerTest {

    private Module module;
    private HttpServletRequest httpServletRequest;
    private RequestFactory requestFactory;
    private ResponseFactory responseFactory;
    private WebSocketListener listener;

    @Before
    public void setUp() throws Exception {
        module = mock(Module.class);
        httpServletRequest = mock(HttpServletRequest.class);
        requestFactory = mock(RequestFactory.class);
        responseFactory = mock(ResponseFactory.class);
        listener = new DaggerWebSocketListener(module, httpServletRequest, requestFactory, responseFactory);
    }

    private Request given_that_a_websocket_open_request_will_be_created() {
        Request request = mock(Request.class);
        when(requestFactory.createWebSocketOpenRequest(httpServletRequest)).thenReturn(request);
        return request;
    }

    private Request given_that_a_websocket_close_request_will_be_created() {
        Request request = mock(Request.class);
        when(requestFactory.createWebSocketCloseRequest(httpServletRequest)).thenReturn(request);
        return request;
    }

    private Request given_that_a_websocket_message_request_will_be_created_for(String message) {
        Request request = mock(Request.class);
        when(requestFactory.createWebSocketMessageRequest(httpServletRequest, message)).thenReturn(request);
        return request;
    }

    private Response given_that_a_response_will_be_created_from(WebSocket webSocket) {
        Response response = mock(Response.class);
        when(responseFactory.create(webSocket)).thenReturn(response);
        return response;
    }

    private Reaction given_that_a_reaction_will_be_produced_when_request_is_handled(Request request) throws Exception {
        RequestHandler requestHandler = mock(RequestHandler.class);
        Reaction reaction = mock(Reaction.class);
        when(module.getHandlerFor(request)).thenReturn(requestHandler);
        when(requestHandler.handle(request)).thenReturn(reaction);
        return reaction;
    }

    @Test
    public void test_on_connect() throws Throwable {
        WebSocket webSocket = mock(WebSocket.class);
        Request request = given_that_a_websocket_open_request_will_be_created();
        Response response = given_that_a_response_will_be_created_from(webSocket);
        Reaction reaction = given_that_a_reaction_will_be_produced_when_request_is_handled(request);

        listener.onConnect(webSocket);

        verify(reaction).execute(request, response);
    }

    @Test
    public void test_on_close() throws Throwable {
        WebSocket webSocket = mock(WebSocket.class);
        Request request = given_that_a_websocket_close_request_will_be_created();
        Response response = given_that_a_response_will_be_created_from(webSocket);
        Reaction reaction = given_that_a_reaction_will_be_produced_when_request_is_handled(request);

        listener.onClose(webSocket, null);

        verify(reaction).execute(request, response);
    }

    @Test
    public void test_on_message() throws Throwable {
        WebSocket webSocket = mock(WebSocket.class);
        Request request = given_that_a_websocket_message_request_will_be_created_for("Hello there");
        Response response = given_that_a_response_will_be_created_from(webSocket);
        Reaction reaction = given_that_a_reaction_will_be_produced_when_request_is_handled(request);

        listener.onMessage(webSocket, "Hello there");

        verify(reaction).execute(request, response);
    }

}
