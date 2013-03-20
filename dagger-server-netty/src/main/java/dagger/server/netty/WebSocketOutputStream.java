package dagger.server.netty;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WebSocketOutputStream extends ByteArrayOutputStream {

    private Channel channel;

    public WebSocketOutputStream() {
    }

    public WebSocketOutputStream(Channel channel) {
        setChannel(channel);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public synchronized void flush() throws IOException {
        if(channel == null || size() == 0)
            return;

        String text = new String(toByteArray());
        TextWebSocketFrame frame = new TextWebSocketFrame(text);
        channel.write(frame);
        reset();
    }

}
