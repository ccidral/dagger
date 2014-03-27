package dagger.servlet3.features.websocket;

import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;
import dagger.lang.NotImplementedYet;

import javax.websocket.Session;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class WebSocketResponse implements Response {

    private final OutputStream outputStream;
    private final Session session;
    private StatusCode statusCode;

    public WebSocketResponse(Session session) {
        this.session = session;
        this.outputStream = new WebSocketOutputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setHeader(String name, String value) {
        throw new NotImplementedYet();
    }

    @Override
    public void addCookie(Cookie cookie) {
        throw new NotImplementedYet();
    }

    private void closeSession() throws IOException {
        session.close();
    }

    private class WebSocketOutputStream extends OutputStream {

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        @Override
        public synchronized void write(int b) throws IOException {
            buffer.write(b);
        }

        @Override
        public synchronized void flush() throws IOException {
            session.getBasicRemote().sendText(buffer.toString());
            buffer.reset();
        }

        @Override
        public void close() throws IOException {
            WebSocketResponse.this.closeSession();
        }

    }

}
