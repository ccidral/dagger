package dagger.servlet3.plugin.websocket.grizzly19;

import dagger.Module;
import dagger.RequestHandler;
import dagger.handlers.ResourceNotFound;
import dagger.http.HttpMethod;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DaggerWebSocketApp_isApplicationRequest_Test {

    private DaggerWebSocketApp webSocketApp;
    private Module module;
    private RequestFactory requestFactory;
    private com.sun.grizzly.tcp.Request grizzlyRequest;
    private Request daggerRequest;

    @Before
    public void setUp() throws Exception {
        module = mock(Module.class);
        requestFactory = mock(RequestFactory.class);
        webSocketApp = new DaggerWebSocketApp(module, requestFactory, null, null);

        grizzlyRequest = new com.sun.grizzly.tcp.Request();
        daggerRequest = mock(Request.class);

        when(requestFactory.createFrom(grizzlyRequest, HttpMethod.WEBSOCKET_OPEN)).thenReturn(daggerRequest);
        when(module.getHandlerFor(null)).thenThrow(new IllegalArgumentException("The 'request' parameter should not be null"));
    }

    @Test
    public void test_cannot_handle_requests_that_do_not_contain_the_Upgrade_header() {
        when(daggerRequest.getHeader("Upgrade")).thenReturn(null);
        assertEquals(false, webSocketApp.isApplicationRequest(grizzlyRequest));
    }

    @Test
    public void test_cannot_handle_requests_that_contain_Upgrade_header_other_than_websocket() {
        when(daggerRequest.getHeader("Upgrade")).thenReturn("foobar");
        assertEquals(false, webSocketApp.isApplicationRequest(grizzlyRequest));
    }

    @Test
    public void test_cannot_handle_request_when_no_handler_is_found() {
        when(daggerRequest.getHeader("Upgrade")).thenReturn("websocket");
        when(module.getHandlerFor(daggerRequest)).thenReturn(null);
        assertEquals(false, webSocketApp.isApplicationRequest(grizzlyRequest));
    }

    @Test
    public void test_cannot_handle_request_when_handler_is_ResourceNotFound() {
        when(daggerRequest.getHeader("Upgrade")).thenReturn("websocket");
        when(module.getHandlerFor(daggerRequest)).thenReturn(new ResourceNotFound());
        assertEquals(false, webSocketApp.isApplicationRequest(grizzlyRequest));
    }

    @Test
    public void test_can_handle_request_when_handler_is_found_for_it() {
        RequestHandler handler = mock(RequestHandler.class);
        when(daggerRequest.getHeader("Upgrade")).thenReturn("websocket");
        when(module.getHandlerFor(daggerRequest)).thenReturn(handler);
        assertEquals(true, webSocketApp.isApplicationRequest(grizzlyRequest));
    }

}
