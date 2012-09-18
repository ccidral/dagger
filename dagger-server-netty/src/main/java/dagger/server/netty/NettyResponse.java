package dagger.server.netty;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import dagger.http.Response;
import dagger.http.StatusCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.io.OutputStream;

public class NettyResponse implements Response {

    private final HttpResponse response;
    private final ByteBuf buffer;

    public NettyResponse(HttpResponse response) {
        this.response = response;
        this.buffer = Unpooled.buffer();

        response.setContent(buffer);
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
    public String getContentType() {
        return response.getHeader(CONTENT_TYPE);
    }

    @Override
    public void setContentType(String contentType) {
        response.setHeader(CONTENT_TYPE, contentType);
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
