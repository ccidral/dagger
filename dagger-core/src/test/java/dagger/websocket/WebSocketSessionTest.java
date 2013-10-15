package dagger.websocket;

import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class WebSocketSessionTest {

    @Test
    public void test_get_request() {
        Request request = mock(Request.class);
        WebSocketSession webSocketSession = new DefaultWebSocketSession(request, mock(Response.class));
        assertSame(request, webSocketSession.getRequest());
    }

    @Test
    public void test_write_text_message_to_response_output_stream() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Response response = response(outputStream);
        WebSocketSession webSocketSession = new DefaultWebSocketSession(mock(Request.class), response);

        webSocketSession.write("Hello there!");

        assertEquals("Written message", "Hello there!", new String(outputStream.toByteArray()));
    }

    @Test
    public void test_normal_session_close() throws Throwable {
        Response response = response(mock(OutputStream.class));
        WebSocketSession webSocketSession = new DefaultWebSocketSession(mock(Request.class), response);

        webSocketSession.close();

        assertWebSocketIsClosedWithStatusCode(StatusCode.WEBSOCKET_NORMAL_CLOSE, response);
    }

    @Test
    public void test_close_session_with_arbitrary_status_code() throws Throwable {
        Response response = response(mock(OutputStream.class));
        WebSocketSession webSocketSession = new DefaultWebSocketSession(mock(Request.class), response);

        webSocketSession.close(StatusCode.WEBSOCKET_UNEXPECTED_CONDITION);

        assertWebSocketIsClosedWithStatusCode(StatusCode.WEBSOCKET_UNEXPECTED_CONDITION, response);
    }

    private void assertWebSocketIsClosedWithStatusCode(StatusCode statusCode, Response response) throws IOException {
        OutputStream outputStream = response.getOutputStream();
        InOrder inOrder = inOrder(response, outputStream);
        inOrder.verify(response).setStatusCode(statusCode);
        inOrder.verify(outputStream).close();
    }

    private Response response(OutputStream outputStream) {
        Response response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        return response;
    }

}
