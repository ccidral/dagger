package dagger.servlet3.features.websocket;

import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class WebSocketResponseTest {

    private Response response;
    private Session session;
    private RemoteEndpoint.Basic basicRemote;

    @Before
    public void setUp() {
        session = mock(Session.class);
        basicRemote = mock(RemoteEndpoint.Basic.class);

        when(session.getBasicRemote()).thenReturn(basicRemote);

        response = new WebSocketResponse(session);
    }

    @Test
    public void test_set_status_code() {
        response.setStatusCode(StatusCode.WEBSOCKET_UNEXPECTED_CONDITION);
        assertEquals(StatusCode.WEBSOCKET_UNEXPECTED_CONDITION, response.getStatusCode());
    }

    @Test
    public void test_output_stream_is_not_null() {
        assertNotNull(response.getOutputStream());
    }

    @Test
    public void test_do_not_send_anything_to_the_remote_endpoint_if_output_stream_is_not_flushed()throws Exception {
        response.getOutputStream().write('H');
        response.getOutputStream().write('e');
        response.getOutputStream().write('l');
        response.getOutputStream().write('l');
        response.getOutputStream().write('o');

        verifyNoMoreInteractions(basicRemote);
    }

    @Test
    public void test_send_message_to_remote_endpoint_when_writing_and_flushing_the_output_stream() throws Exception {
        response.getOutputStream().write('H');
        response.getOutputStream().write('e');
        response.getOutputStream().write('l');
        response.getOutputStream().write('l');
        response.getOutputStream().write('o');

        response.getOutputStream().flush();

        verify(basicRemote).sendText("Hello");
    }

    @Test
    public void test_make_sure_it_does_not_send_the_same_message_again() throws Exception {
        response.getOutputStream().write('H');
        response.getOutputStream().write('e');
        response.getOutputStream().write('l');
        response.getOutputStream().write('l');
        response.getOutputStream().write('o');
        response.getOutputStream().flush();

        verify(basicRemote).sendText("Hello");

        response.getOutputStream().write('W');
        response.getOutputStream().write('o');
        response.getOutputStream().write('r');
        response.getOutputStream().write('l');
        response.getOutputStream().write('d');
        response.getOutputStream().flush();

        verify(basicRemote).sendText("World");
    }

    @Test
    public void test_closing_the_output_stream_should_close_the_websocket_session() throws Exception {
        response.getOutputStream().close();
        verify(session).close();
    }

}
