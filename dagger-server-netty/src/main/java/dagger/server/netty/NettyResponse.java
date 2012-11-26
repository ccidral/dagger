package dagger.server.netty;

import dagger.http.HttpHeaderNames;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.lang.time.Clock;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NettyResponse implements Response {

    private final HttpResponse response;
    private final ByteBuf buffer;

    public NettyResponse(HttpResponse response, Clock clock) {
        this.response = response;
        this.buffer = Unpooled.buffer();

        response.setContent(buffer);
        setHeader(HttpHeaderNames.DATE, new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US).format(clock.now()));
    }

    @Override
    public StatusCode getStatusCode() {
        return StatusCode.get(response.getStatus().getCode());
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        response.setStatus(HttpResponseStatus.valueOf(statusCode.getNumber()));
    }

    @Override
    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    @Override
    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                buffer.writeByte(oneByte);
            }
        };
    }

}
