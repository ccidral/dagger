package dagger.reactions;

import dagger.Reaction;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InternalServerErrorTest {

    private Response response;
    private Reaction reaction;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        response = mock(Response.class);
        outputStream = new ByteArrayOutputStream();
        reaction = new InternalServerError();

        when(response.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    public void testStatusCode() throws Exception {
        reaction.execute(null, response);
        verify(response).setStatusCode(StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testTextWrittenInTheResponse() throws Exception {
        reaction.execute(null, response);
        assertEquals("Internal server error", new String(outputStream.toByteArray()));
    }

}
