package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeader;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.mime.MimeType;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.*;

public class OkTest {

    private Response response;

    @Before
    public void setUp() throws Exception {
        response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
    }

    private String textWrittenTo(Response response) {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) response.getOutputStream();
        return new String(outputStream.toByteArray());
    }

    private byte[] bytesWrittenTo(Response response) {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) response.getOutputStream();
        return outputStream.toByteArray();
    }

    @Test
    public void test_status_code_is_200_OK() throws Throwable {
        Reaction reaction = new Ok();
        reaction.execute(null, response);
        verify(response).setStatusCode(StatusCode.OK);
    }

    @Test
    public void test_write_text_to_response() throws Exception {
        Reaction reaction = new Ok("Some text");
        reaction.execute(null, response);
        assertEquals("Some text", textWrittenTo(response));
    }

    @Test
    public void test_default_content_type_is_text_plain() throws Exception {
        Reaction reaction = new Ok("Any text");
        reaction.execute(null, response);
        verify(response).setHeader(HttpHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN);
    }

    @Test
    public void test_write_text_to_response_and_set_content_type_header() throws Exception {
        Reaction reaction = new Ok("{}", "application/json");
        reaction.execute(null, response);
        assertEquals("{}", textWrittenTo(response));
        verify(response).setHeader(HttpHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON);
    }

    @Test
    public void test_write_byte_array_to_response() throws Exception {
        byte[] theByteArray = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        Reaction reaction = new Ok(theByteArray, MimeType.IMAGE_PNG);
        reaction.execute(null, response);
        assertArrayEquals(theByteArray, bytesWrittenTo(response));
    }

    @Test
    public void test_set_http_headers() throws Exception {
        Ok ok = new Ok("Test");
        ok.setHeader("Car", "mustang");
        ok.setHeader("Fruit", "apple");

        ok.execute(null, response);

        verify(response).setHeader("Car", "mustang");
        verify(response).setHeader("Fruit", "apple");
    }

}
