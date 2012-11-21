package dagger.server.netty;

import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class NettyResponseTest {

    private MockNettyHttpResponse mockNettyHttpResponse;
    private Response response;

    @Before
    public void setUp() throws Exception {
        mockNettyHttpResponse = new MockNettyHttpResponse();
        response = new NettyResponse(mockNettyHttpResponse);
    }

    @Test
    public void testOutputStreamIsNotNull() throws IOException {
        assertNotNull(response.getOutputStream());
    }

    @Test
    public void testWriteToOutputStream() throws IOException {
        response.getOutputStream().write("hello world".getBytes());

        assertNotNull(mockNettyHttpResponse.getContent());
        assertEquals("hello world", mockNettyHttpResponse.getWrittenText());
    }

    @Test
    public void testSetStatusCode() {
        response.setStatusCode(StatusCode.OK);

        assertNotNull(mockNettyHttpResponse.getStatus());
        assertEquals(StatusCode.OK.getNumber(), mockNettyHttpResponse.getStatus().getCode());
    }

    @Test
    public void testGetStatusCode() {
        response.setStatusCode(StatusCode.NOT_FOUND);
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSetHeader() {
        response.setHeader("Fruit", "apple");
        assertEquals("apple", mockNettyHttpResponse.getHeader("Fruit"));
    }

}
