package dagger.server.netty;

import dagger.http.*;
import dagger.lang.time.Clock;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NettyResponse implements Response {

    private final HttpResponse response;
    private final ByteBuf buffer;

    public NettyResponse(HttpResponse response, Clock clock) {
        this.response = response;
        this.buffer = Unpooled.buffer();

        response.setContent(buffer);
        setHeader(HttpHeaderNames.DATE, Formats.timestamp().format(clock.now()));
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
    public void setCookie(String name, String value) {
        validateCookie(name, value);
        setCookieWithoutValidatingIt(name, value);
    }

    @Override
    public void setCookie(String name, String value, CookieOptions options) {
        validateCookie(name, value);
        String optionsString = options.getOptionsString();
        String valueWithOptions = value + (optionsString != null ? "; " + optionsString : "");
        setCookieWithoutValidatingIt(name, valueWithOptions);
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

    private void validateCookie(String name, String value) {
        if(name.contains("=")) throw new IllegalArgumentException("Cookie name cannot contain the equals sign (=)");
        if(value.contains(",")) throw new IllegalArgumentException("Cookie value cannot contain commas (,)");
        if(value.contains(";")) throw new IllegalArgumentException("Cookie value cannot contain semicolons (;)");
        if(value.contains(" ")) throw new IllegalArgumentException("Cookie value cannot contain whitespaces");
    }

    private void setCookieWithoutValidatingIt(String name, String value) {
        List<String> allOtherCookieHeaders = getAllCookiesExcept(name);
        allOtherCookieHeaders.add(name + "=" + value);
        response.setHeader("Set-Cookie", allOtherCookieHeaders);
    }

    private List<String> getAllCookiesExcept(String exceptionCookieName) {
        List<String> allCookieHeaders = new ArrayList<>(response.getHeaders("Set-Cookie"));
        Iterator<String> iterator = allCookieHeaders.iterator();
        while(iterator.hasNext()) {
            String cookieHeader = iterator.next();
            if(cookieHeader.startsWith(exceptionCookieName + "=")) {
                iterator.remove();
                break;
            }
        }
        return allCookieHeaders;
    }

}
