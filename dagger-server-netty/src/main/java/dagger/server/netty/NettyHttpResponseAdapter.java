package dagger.server.netty;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import dagger.http.Response;
import dagger.http.StatusCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class NettyHttpResponseAdapter implements Response {

    private final HttpResponse response;
    private final ByteBuf buffer;

    public NettyHttpResponseAdapter(HttpResponse response) {
        this.response = response;
        this.buffer = Unpooled.buffer();

        response.setContent(buffer);
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        response.setStatus(HttpResponseStatus.valueOf(statusCode.getNumber()));
    }

    @Override
    public void write(String text) {
        buffer.writeBytes(text.getBytes());
    }

    @Override
    public void setContentType(String contentType) {
        response.setHeader(CONTENT_TYPE, contentType);
    }

}
