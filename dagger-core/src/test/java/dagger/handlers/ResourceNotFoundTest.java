package dagger.handlers;

import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.HttpHeader;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResourceNotFoundTest {

    private RequestHandler requestHandler;

    @Before
    public void setUp() throws Exception {
        requestHandler = new ResourceNotFound();
    }

    @Test
    public void test_can_handle_regular_request() {
        Request request = mock(Request.class);
        assertTrue(requestHandler.canHandle(request));
    }

    @Test
    public void test_cannot_handle_requests_containing_Upgrade_header() {
        Request request = mock(Request.class);
        when(request.getHeader(HttpHeader.UPGRADE)).thenReturn("foobar");
        assertFalse(requestHandler.canHandle(request));
    }

    @Test
    public void test_reaction_is_not_null() throws Throwable {
        Reaction reaction = requestHandler.handle(null);
        assertNotNull(reaction);
    }

    @Test
    public void test_reaction_sets_status_code_as_404_NOT_FOUND() throws Throwable {
        Response response = createResponse();
        Reaction reaction = requestHandler.handle(null);
        reaction.execute(null, response);
        verify(response).setStatusCode(StatusCode.NOT_FOUND);
    }

    private Response createResponse() {
        Response response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        return response;
    }

}
