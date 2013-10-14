package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocket;
import dagger.http.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class GrizzlyWebSocketResponseTest {

    private WebSocket webSocket;
    private Response response;

    @Before
    public void setUp() throws Exception {
        webSocket = mock(WebSocket.class);
        response = new GrizzlyWebSocketResponse(webSocket);
    }

    @Test
    public void test_output_stream_is_not_null() {
        assertNotNull(response.getOutputStream());
    }

    @Test
    public void test_do_not_send_message_to_websocket_when_output_stream_is_not_flushed() throws Throwable {
        OutputStream outputStream = response.getOutputStream();
        outputStream.write("Hello world".getBytes());
        verify(webSocket, never()).send(anyString());
    }

    @Test
    public void test_send_message_to_websocket_when_output_stream_is_flushed() throws Throwable {
        OutputStream outputStream = response.getOutputStream();
        outputStream.write("Hello there!".getBytes());
        outputStream.flush();
        verify(webSocket).send("Hello there!");
    }

    @Test
    public void test_send_two_messages_to_websocket() throws Throwable {
        OutputStream outputStream = response.getOutputStream();

        outputStream.write("Hello world".getBytes());
        outputStream.flush();

        outputStream.write("Foo bar".getBytes());
        outputStream.flush();

        verify(webSocket).send("Hello world");
        verify(webSocket).send("Foo bar");
    }

}
