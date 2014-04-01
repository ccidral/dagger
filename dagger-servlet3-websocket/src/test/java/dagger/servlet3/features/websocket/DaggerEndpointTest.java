package dagger.servlet3.features.websocket;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.HttpMethod;
import dagger.http.Request;
import dagger.http.Response;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class DaggerEndpointTest {

    private WebSocketRequestFactory requestFactory;
    private WebSocketResponseFactory responseFactory;
    private Module module;
    private Endpoint endpoint;
    private MockSession session;
    private EndpointConfig endpointConfig;

    @Before
    public void setUp() throws Exception {
        module = mock(Module.class);
        requestFactory = mock(WebSocketRequestFactory.class);
        responseFactory = mock(WebSocketResponseFactory.class);
        endpoint = new DaggerEndpoint(module, requestFactory, responseFactory);
        session = new MockSession();
        endpointConfig = mock(EndpointConfig.class);
    }

    @Test
    public void test_on_open_copy_request_headers_from_endpoint_config_to_the_session() throws Exception {
        new Expectations(HttpMethod.WEBSOCKET_OPEN);
        endpoint.onOpen(session, endpointConfig);
        assertSame(
            endpointConfig.getUserProperties().get(DaggerEndpointConfigurator.REQUEST_HEADERS_KEY),
            session.getUserProperties().get(DaggerEndpointConfigurator.REQUEST_HEADERS_KEY)
        );
    }

    @Test
    public void test_on_open_handles_request_and_executes_reaction() throws Exception {
        Expectations onOpen = new Expectations(HttpMethod.WEBSOCKET_OPEN);
        endpoint.onOpen(session, endpointConfig);
        onOpen.assertReactionIsExecuted();
    }

    @Test
    public void test_on_close_handles_request_and_executes_reaction() throws Exception {
        Expectations onClose = new Expectations(HttpMethod.WEBSOCKET_CLOSE);
        endpoint.onClose(session, null);
        onClose.assertReactionIsExecuted();
    }

    @Test
    public void test_on_message_handles_request_and_executes_reaction() throws Exception {
        Expectations onOpen = new Expectations(HttpMethod.WEBSOCKET_OPEN);
        endpoint.onOpen(session, endpointConfig);
        onOpen.assertReactionIsExecuted();

        Expectations onMessage = new Expectations(HttpMethod.WEBSOCKET_MESSAGE, "Hello world");
        session.fireOnMessageEvent("Hello world");
        onMessage.assertReactionIsExecuted();
    }

    @Test
    public void test_on_open_closes_session_if_it_cannot_handle_the_request() throws Exception {
        Expectations onOpen = new Expectations(HttpMethod.WEBSOCKET_OPEN)
            .cannotHandleRequest();

        endpoint.onOpen(session, endpointConfig);

        onOpen.assertRequestIsNotHandled();
        session
            .assertClosed()
            .assertNoMessageHandlerIsAdded();
    }

    private class Expectations {

        private final Request request = mock(Request.class);
        private final Response response = mock(Response.class);
        private final Reaction reaction = mock(Reaction.class);
        private final RequestHandler requestHandler = mock(RequestHandler.class);

        public Expectations(String httpMethod) throws Exception {
            this(httpMethod, null);
        }

        public Expectations(String httpMethod, String requestBody) throws Exception {
            when(requestFactory.create(httpMethod, requestBody, session)).thenReturn(request);
            when(responseFactory.create(session)).thenReturn(response);
            when(module.getHandlerFor(request)).thenReturn(requestHandler);
            when(requestHandler.canHandle(request)).thenReturn(true);
            when(requestHandler.handle(request)).thenReturn(reaction);
            when(endpointConfig.getUserProperties()).thenReturn(new HashMap<String, Object>());
            endpointConfig.getUserProperties().put(DaggerEndpointConfigurator.REQUEST_HEADERS_KEY, new Object());
        }

        public Expectations cannotHandleRequest() {
            when(requestHandler.canHandle(request)).thenReturn(false);
            return this;
        }

        public Expectations assertReactionIsExecuted() throws Exception {
            verify(reaction).execute(request, response);
            return this;
        }

        public Expectations assertRequestIsNotHandled() throws Exception {
            verify(requestHandler, never()).handle(any(Request.class));
            return this;
        }

    }

}
