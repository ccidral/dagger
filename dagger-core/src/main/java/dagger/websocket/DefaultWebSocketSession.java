package dagger.websocket;

import dagger.DaggerRuntimeException;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DefaultWebSocketSession implements WebSocketSession {

    private final Response response;

    public DefaultWebSocketSession(Response response) {
        this.response = response;
    }

    @Override
    public void write(String message) {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            throw new DaggerRuntimeException(e);
        }
    }

    @Override
    public void close() {
        close(StatusCode.WEBSOCKET_NORMAL_CLOSE);
    }

    @Override
    public void close(StatusCode statusCode) {
        try {
            response.setStatusCode(statusCode);
            response.getOutputStream().close();
        } catch (IOException e) {
            throw new DaggerRuntimeException(e);
        }
    }

}
