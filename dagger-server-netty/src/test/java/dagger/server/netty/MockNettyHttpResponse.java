package dagger.server.netty;

import dagger.lang.NotImplementedYet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

class MockNettyHttpResponse implements FullHttpResponse {

    private final ByteBuf byteBuf;
    private final MockHeaders headers = new MockHeaders();
    private HttpResponseStatus httpResponseStatus;

    public MockNettyHttpResponse() {
        byteBuf = Unpooled.buffer();
    }

    public String getWrittenText() {
        return new String(byteBuf.array(), 0, byteBuf.readableBytes());
    }

    @Override
    public ByteBuf data() {
        return this.byteBuf;
    }

    @Override
    public HttpResponseStatus getStatus() {
        return httpResponseStatus;
    }

    @Override
    public FullHttpResponse setStatus(HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatus = httpResponseStatus;
        return this;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }


    // ~~~ NOT IMPLEMENTED YET ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //


    @Override
    public DecoderResult getDecoderResult() {
        throw new NotImplementedYet();
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {
        throw new NotImplementedYet();
    }

    @Override
    public HttpHeaders trailingHeaders() {
        throw new NotImplementedYet();
    }

    @Override
    public FullHttpResponse copy() {
        throw new NotImplementedYet();
    }

    @Override
    public FullHttpResponse retain(int i) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean release() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean release(int i) {
        throw new NotImplementedYet();
    }

    @Override
    public int refCnt() {
        throw new NotImplementedYet();
    }

    @Override
    public FullHttpResponse retain() {
        throw new NotImplementedYet();
    }

    @Override
    public HttpVersion getProtocolVersion() {
        throw new NotImplementedYet();
    }

    @Override
    public FullHttpResponse setProtocolVersion(HttpVersion httpVersion) {
        throw new NotImplementedYet();
    }

}
