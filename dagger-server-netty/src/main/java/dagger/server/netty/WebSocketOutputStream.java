package dagger.server.netty;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WebSocketOutputStream extends ByteArrayOutputStream {

    private final Channel channel;

    public WebSocketOutputStream(Channel channel) {
        this.channel = channel;
    }

    @Override
    public synchronized void flush() throws IOException {
        String text = new String(toByteArray());
        TextWebSocketFrame frame = new TextWebSocketFrame(text);
        channel.write(frame);
        reset();
    }

}
