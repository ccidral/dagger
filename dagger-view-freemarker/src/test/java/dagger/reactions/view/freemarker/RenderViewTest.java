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
        Request request = createRequest("/bananas", "/foo/bar");
        Response response = createResponse();
        Reaction reaction = new RenderView("test-with-simple-model", contentType, model);

        reaction.execute(request, response);

        assertEquals("Hello World!", renderedContent(response));
    }

    @Test
    public void test_request_uri_is_accessible_from_the_template() throws Exception {
        String model = "World";
        String contentType = "foo/bar";
        Request request = createRequest("/bananas", "/foo/bar");
        Response response = createResponse();
        Reaction reaction = new RenderView("test-request-uri", contentType, model);

        reaction.execute(request, response);

        assertEquals("The request URI is /foo/bar", renderedContent(response));
    }

    @Test
    public void test_context_path_string_is_accessible_from_the_template() throws Exception {
        String model = "World";
        String contentType = "foo/bar";
        Request request = createRequest("/bananas", "/foo/bar");
        Response response = createResponse();
        Reaction reaction = new RenderView("test-context-path", contentType, model);

        reaction.execute(request, response);

        assertEquals("The context path is /bananas", renderedContent(response));
    }

    @Test
    public void test_uri_function_prepends_the_context_path() throws Exception {
        String contextPath = "/bananas";
        String model = "/are/cheap";
        Request request = createRequest(contextPath, "/foo/bar");
        Response response = createResponse();

        Reaction reaction = new RenderView("test-uri-function", "text/plain", model);
        reaction.execute(request, response);

        assertEquals("The URI is /bananas/are/cheap", renderedContent(response));
    }

    @Test
    public void test_content_type_header_is_set_on_the_response() throws Exception {
        String model = "World";
        String contentType = "foo/bar";
        Request request = createRequest("/bananas", "/foo/bar");
        Response response = createResponse();
        Reaction reaction = new RenderView("test-with-simple-model", contentType, model);

        reaction.execute(request, response);

        verify(response).setHeader(CONTENT_TYPE, contentType);
    }

    private Request createRequest(String contextPath, String uri) {
        Request request = mock(Request.class);
        when(request.getContextPath()).thenReturn(contextPath);
        when(request.getURI()).thenReturn(uri);
        return request;
    }

    private Response createResponse() {
        Response response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        return response;
    }

    private String renderedContent(Response response) {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) response.getOutputStream();
        return new String(outputStream.toByteArray());
    }

}
