package dagger.server.netty;

import dagger.lang.NotImplementedYet;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpTransferEncoding;
import io.netty.handler.codec.http.HttpVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MockNettyHttpResponse implements HttpResponse {

    private ByteBuf byteBuf;
    private HttpResponseStatus httpResponseStatus;
    private Map headers = new HashMap();

    public String getWrittenText() {
        return new String(byteBuf.array(), 0, byteBuf.readableBytes());
    }

    @Override
    public ByteBuf getContent() {
        return this.byteBuf;
    }

    @Override
    public void setContent(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public HttpResponseStatus getStatus() {
        return httpResponseStatus;
    }

    @Override
    public void setStatus(HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatus = httpResponseStatus;
    }

    @Override
    public String getHeader(String s) {
        if(headers.containsKey(s))
            return headers.get(s).toString();

        return null;
    }

    @Override
    public void setHeader(String s, Object o) {
        headers.put(s, o);
    }


    // ~~~ NOT IMPLEMENTED YET ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    @Override
    public List<String> getHeaders(String s) {
        throw new NotImplementedYet();
    }

    @Override
    public List<Map.Entry<String, String>> getHeaders() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean containsHeader(String s) {
        throw new NotImplementedYet();
    }

    @Override
    public Set<String> getHeaderNames() {
        throw new NotImplementedYet();
    }

    @Override
    public HttpVersion getProtocolVersion() {
        throw new NotImplementedYet();
    }

    @Override
    public void setProtocolVersion(HttpVersion httpVersion) {
        throw new NotImplementedYet();
    }

    @Override
    public void addHeader(String s, Object o) {
        throw new NotImplementedYet();
    }

    @Override
    public void setHeader(String s, Iterable<?> objects) {
        throw new NotImplementedYet();
    }

    @Override
    public void removeHeader(String s) {
        throw new NotImplementedYet();
    }

    @Override
    public void clearHeaders() {
        throw new NotImplementedYet();
    }

    @Override
    public HttpTransferEncoding getTransferEncoding() {
        throw new NotImplementedYet();
    }

    @Override
    public void setTransferEncoding(HttpTransferEncoding httpTransferEncoding) {
        throw new NotImplementedYet();
    }

}
