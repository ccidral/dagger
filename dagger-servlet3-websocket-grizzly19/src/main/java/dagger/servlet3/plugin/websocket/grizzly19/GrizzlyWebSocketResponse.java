package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocket;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class GrizzlyWebSocketResponse implements Response {

    private final WebSocket webSocket;
    private StatusCode statusCode = StatusCode.WEBSOCKET_NORMAL_CLOSE;

    public GrizzlyWebSocketResponse(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public OutputStream getOutputStream() {
        return new WebSocketOutputStream();
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        if(statusCode == null)
            throw new IllegalArgumentException("Parameter 'statusCode' must not be null");
        this.statusCode = statusCode;
    }

    @Override
    public void setHeader(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCookie(Cookie cookie) {
        throw new UnsupportedOperationException();
    }

    private class WebSocketOutputStream extends ByteArrayOutputStream {
        @Override
        public synchronized void flush() throws IOException {
            String message = toString(Charset.defaultCharset().name());
            webSocket.send(message);
            reset();
        }

        @Override
        public void close() throws IOException {
            webSocket.close(statusCode.getCode());
        }
    }

}
