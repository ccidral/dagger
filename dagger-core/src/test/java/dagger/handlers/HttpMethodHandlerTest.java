package dagger.handlers;

import dagger.Action;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.Route;
import dagger.http.HttpHeader;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpMethodHandlerTest {

    private Action action;
    private RequestHandler requestHandler;

    @Before
    public void setUp() throws Exception {
        action = mock(Action.class);
        requestHandler = new HttpMethodRequestHandler("GET", route("/foo/bar"), action);
    }

    private Route route(String spec) {
        Route route = mock(Route.class);
        when(route.matches(spec)).thenReturn(true);
        return route;
    }

    private Request request(String httpMethod, String uri) {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(httpMethod);
        when(request.getURI()).thenReturn(uri);
        return request;
    }

    @Test
    public void test_can_handle_request_that_matches_both_http_method_and_route() {
        assertTrue(requestHandler.canHandle(request("GET", "/foo/bar")));
    }

    @Test
    public void test_can_handle_request_that_matches_http_method_regardless_of_character_casing() {
        assertTrue(requestHandler.canHandle(request("get", "/foo/bar")));
    }

    @Test
    public void test_cannot_handle_request_with_mismatching_http_method() {
        assertFalse(requestHandler.canHandle(request("POST", "/foo/bar")));
    }

    @Test
    public void test_cannot_handle_request_with_mismatching_route() {
        assertFalse(requestHandler.canHandle(request("GET", "/hello")));
    }

    @Test
    public void test_cannot_handle_request_with_UPGRADE_header() {
        Request request = request("GET", "/foo/bar");
        when(request.getHeader(HttpHeader.UPGRADE)).thenReturn("Something");
        assertFalse(requestHandler.canHandle(request));
    }

    @Test
    public void test_execute_action_when_handling_request() throws Exception {
        Request request = request("GET", "/foo/bar");
        requestHandler.handle(request);
        verify(action).execute(request);
    }

    @Test
    public void test_return_reaction_produced_by_action_when_handling_request() throws Exception {
        Reaction reaction = mock(Reaction.class);
        Request request = request("GET", "/foo/bar");
        when(action.execute(request)).thenReturn(reaction);
        assertSame(reaction, requestHandler.handle(request));
    }

}
