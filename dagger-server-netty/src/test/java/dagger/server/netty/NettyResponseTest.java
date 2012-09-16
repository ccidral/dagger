package dagger.server.netty;

import dagger.http.Response;
import dagger.http.StatusCode;
import io.netty.handler.codec.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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
        assertThat(response.getOutputStream(), is(notNullValue()));
    }

    @Test
    public void testWriteToOutputStream() throws IOException {
        response.getOutputStream().write("hello world".getBytes());

        assertThat(mockNettyHttpResponse.getContent(), is(notNullValue()));
        assertThat(mockNettyHttpResponse.getWrittenText(), equalTo("hello world"));
    }

    @Test
    public void testSetStatusCode() {
        response.setStatusCode(StatusCode.OK);

        assertThat(mockNettyHttpResponse.getStatus(), is(notNullValue()));
        assertThat(mockNettyHttpResponse.getStatus().getCode(), equalTo(StatusCode.OK.getNumber()));
    }

    @Test
    public void testSetContentType() {
        response.setContentType("text/html");

        assertThat(mockNettyHttpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE), equalTo("text/html"));
    }

}
