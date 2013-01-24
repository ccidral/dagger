package dagger.server.netty;

import dagger.lang.NotImplementedYet;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpTransferEncoding;
import io.netty.handler.codec.http.HttpVersion;

import java.util.*;

class MockNettyHttpResponse implements HttpResponse {

    private ByteBuf byteBuf;
    private HttpResponseStatus httpResponseStatus;
    private Map<String, List<Object>> headers = new HashMap<>();

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
    public String getHeader(String name) {
        if(headers.containsKey(name))
            return headers.get(name).get(0).toString();

        return null;
    }

    @Override
    public void setHeader(String name, Object value) {
        List<Object> values = new ArrayList<>();
        values.add(value);
        headers.put(name, values);
    }


    // ~~~ NOT IMPLEMENTED YET ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    @Override
    public List<String> getHeaders(String headerName) {
        List<String> list = new ArrayList<>();
        if(headers.containsKey(headerName))
            for(Object value : headers.get(headerName))
                list.add(value.toString());
        return Collections.unmodifiableList(list);
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
    public void addHeader(String name, Object value) {
        List<Object> values = headers.get(name);
        if(values == null) {
            values = new ArrayList<>();
            headers.put(name, values);
        }
        values.add(value);
    }

    @Override
    public void setHeader(String name, Iterable<?> values) {
        Iterator<?> iterator = values.iterator();
        ArrayList<Object> list = new ArrayList<>();
        while(iterator.hasNext())
            list.add(iterator.next());
        headers.put(name, list);
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
