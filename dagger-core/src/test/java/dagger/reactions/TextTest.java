package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeader;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.mime.MimeType;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TextTest {

    private Response response;

    @Before
    public void setUp() throws Throwable {
        response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
    }

    private String textWrittenTo(Response response) throws Throwable {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) response.getOutputStream();
        return outputStream.toString();
    }

    @Test
    public void test_status_code_is_200_OK() throws Throwable {
        Reaction reaction = new Text("", MimeType.TEXT_PLAIN);
        reaction.execute(null, response);
        verify(response).setStatusCode(StatusCode.OK);
    }

    @Test
    public void test_set_content_type_header() throws Throwable {
        Reaction reaction = new Text("", MimeType.TEXT_HTML);
        reaction.execute(null, response);
        verify(response).setHeader(HttpHeader.CONTENT_TYPE, MimeType.TEXT_HTML);
    }

    @Test
    public void test_write_text_to_response() throws Throwable {
        Reaction reaction = new Text("Hello world", MimeType.APPLICATION_JSON);
        reaction.execute(null, response);
        assertEquals("Hello world", textWrittenTo(response));
    }

    @Test
    public void test_write_text_from_input_stream_to_response() throws Throwable {
        InputStream inputStream = new ByteArrayInputStream("Hello world from input stream".getBytes());
        Reaction reaction = new Text(inputStream, MimeType.APPLICATION_JSON);
        reaction.execute(null, response);
        assertEquals("Hello world from input stream", textWrittenTo(response));
    }

}
