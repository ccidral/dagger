package dagger.websocket;

import dagger.DaggerRuntimeException;
import dagger.http.Response;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DefaultWebSocketOutput implements WebSocketOutput {

    private final Response response;

    public DefaultWebSocketOutput(Response response) {
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

}
