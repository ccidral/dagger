package dagger.websocket;

import dagger.http.Response;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebSocketSessionTest {

    @Test
    public void test_write_text_message_to_response_output_stream() {
        Response response = mock(Response.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);

        WebSocketSession webSocketSession = new DefaultWebSocketSession(response);
        webSocketSession.write("Hello there!");

        assertEquals("Written message", "Hello there!", new String(outputStream.toByteArray()));
    }

}
