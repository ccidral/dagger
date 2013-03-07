package dagger.server.netty;

import dagger.http.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertSame;

public class NettyWebSocketResponseTest {

    @Before
    public void setUp() {
    }

    @Test
    public void testOutputStream() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Response response = new NettyWebSocketResponse(outputStream);
        assertSame("Output stream", outputStream, response.getOutputStream());
    }


}