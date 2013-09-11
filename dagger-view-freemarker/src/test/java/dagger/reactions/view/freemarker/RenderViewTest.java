package dagger.reactions.view.freemarker;

import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static dagger.http.HttpHeaderNames.CONTENT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RenderViewTest {

    @Test
    public void test_model_is_accessible_from_the_template() throws Exception {
        String model = "World";
        String contentType = "foo/bar";
        Request request = createRequest("/foo/bar");
        Response response = createResponse();
        Reaction reaction = new RenderView("test-with-simple-model", contentType, model);

        reaction.execute(request, response);

        assertEquals("Hello World!", contentWrittenTo(response));
    }

    @Test
    public void test_request_uri_is_accessible_from_the_template() throws Exception {
        String model = "World";
        String contentType = "foo/bar";
        Request request = createRequest("/foo/bar");
        Response response = createResponse();
        Reaction reaction = new RenderView("test-request-uri", contentType, model);

        reaction.execute(request, response);

        assertEquals("The request URI is /foo/bar", contentWrittenTo(response));
    }

    @Test
    public void test_content_type_header_is_set_on_the_response() throws Exception {
        String model = "World";
        String contentType = "foo/bar";
        Request request = createRequest("/foo/bar");
        Response response = createResponse();
        Reaction reaction = new RenderView("test-with-simple-model", contentType, model);

        reaction.execute(request, response);

        verify(response).setHeader(CONTENT_TYPE, contentType);
    }

    private Request createRequest(String uri) {
        Request request = mock(Request.class);
        when(request.getURI()).thenReturn(uri);
        return request;
    }

    private Response createResponse() {
        Response response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        return response;
    }

    private String contentWrittenTo(Response response) {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) response.getOutputStream();
        return new String(outputStream.toByteArray());
    }

}
