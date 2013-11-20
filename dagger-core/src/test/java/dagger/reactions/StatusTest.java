package dagger.reactions;

import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatusTest {

    private Request request;
    private Response response;

    @Before
    public void setUp() throws Exception {
        request = mock(Request.class);
        response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_status_code_cannot_be_null() throws Throwable {
        new Status(null);
    }

    @Test
    public void test_status_code() throws Throwable {
        Reaction reaction = new Status(StatusCode.BAD_REQUEST);
        reaction.execute(request, response);
        verify(response).setStatusCode(StatusCode.BAD_REQUEST);
    }

    @Test
    public void test_message() throws Throwable {
        Reaction reaction = new Status(StatusCode.OK, "Hello world");
        reaction.execute(request, response);
        assertEquals("Hello world", contentsOf(response));
    }

    private String contentsOf(Response response) {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) response.getOutputStream();
        return new String(outputStream.toByteArray());
    }

}
