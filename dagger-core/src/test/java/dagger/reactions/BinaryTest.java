package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeader;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BinaryTest {

    private Response response;

    @Before
    public void setUp() throws Exception {
        response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
    }

    private byte[] bytesWrittenTo(Response response) {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) response.getOutputStream();
        return outputStream.toByteArray();
    }

    @Test
    public void test_status_code_is_200_OK() throws Throwable {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        Reaction reaction = new Binary(inputStream, "image/png");
        reaction.execute(null, response);
        verify(response).setStatusCode(StatusCode.OK);
    }

    @Test
    public void test_set_content_type_header() throws Throwable {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        Reaction reaction = new Binary(inputStream, "image/png");
        reaction.execute(null, response);
        verify(response).setHeader(HttpHeader.CONTENT_TYPE, "image/png");
    }

    @Test
    public void test_write_bytes_from_input_stream_to_response() throws Throwable {
        byte[] theBytes = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        InputStream inputStream = new ByteArrayInputStream(theBytes);
        Reaction reaction = new Binary(inputStream, "image/png");
        reaction.execute(null, response);
        assertArrayEquals(theBytes, bytesWrittenTo(response));
    }

    @Test
    public void test_write_bytes_to_response() throws Throwable {
        byte[] theBytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Reaction reaction = new Binary(theBytes, "image/png");
        reaction.execute(null, response);
        assertArrayEquals(theBytes, bytesWrittenTo(response));
    }

    @Test
    public void test_close_input_stream_after_all_bytes_are_read() throws Throwable {
        InputStream inputStream = spy(new ByteArrayInputStream(new byte[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0}));
        Reaction reaction = new Binary(inputStream, "image/png");
        reaction.execute(null, response);
        verify(inputStream).close();
    }

    @Test
    public void test_close_input_stream_even_when_an_exception_is_thrown() throws Throwable {
        InputStream inputStream = mock(InputStream.class);
        byte[] anyByteArray = any();

        when(inputStream.read(anyByteArray)).thenThrow(new IOException());

        Reaction reaction = new Binary(inputStream, "image/png");
        try {
            reaction.execute(null, response);
        } catch (Exception e) {
        }

        verify(inputStream).close();
    }

}
