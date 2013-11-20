package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocket;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;

import static junit.framework.Assert.assertEquals;
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

    @Test
    public void test_default_status_code_is_WEBSOCKET_NORMAL_CLOSE() {
        assertEquals(StatusCode.WEBSOCKET_NORMAL_CLOSE, response.getStatusCode());
    }

    @Test
    public void test_change_status_code() {
        response.setStatusCode(StatusCode.WEBSOCKET_UNEXPECTED_CONDITION);
        assertEquals(StatusCode.WEBSOCKET_UNEXPECTED_CONDITION, response.getStatusCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_cannot_set_null_status_code() {
        response.setStatusCode(null);
    }

    @Test
    public void test_closing_the_output_stream_should_close_the_websocket() throws Throwable {
        OutputStream outputStream = response.getOutputStream();
        outputStream.close();
        verify(webSocket).close(StatusCode.WEBSOCKET_NORMAL_CLOSE.getCode());
    }

    @Test
    public void test_status_code_should_be_passed_to_websocket_on_close() throws Throwable {
        response.setStatusCode(StatusCode.WEBSOCKET_UNEXPECTED_CONDITION);
        OutputStream outputStream = response.getOutputStream();
        outputStream.close();
        verify(webSocket).close(StatusCode.WEBSOCKET_UNEXPECTED_CONDITION.getCode());
    }

}
