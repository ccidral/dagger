package dagger.server.netty;

import dagger.http.Formats;
import dagger.http.HttpHeaderNames;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;
import dagger.http.cookie.CookieOption;
import dagger.lang.time.Clock;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NettyResponse implements Response {

    private final FullHttpResponse response;
    private final ByteBuf buffer;

    public NettyResponse(FullHttpResponse response, Clock clock) {
        this.response = response;
        this.buffer = response.content();

        setHeader(HttpHeaderNames.DATE, Formats.timestamp().format(clock.now()));
    }

    @Override
    public StatusCode getStatusCode() {
        return StatusCode.get(response.getStatus().code());
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        response.setStatus(HttpResponseStatus.valueOf(statusCode.getNumber()));
    }

    @Override
    public void setHeader(String name, String value) {
        response.headers().set(name, value);
    }

    @Override
    public void setCookie(Cookie cookie) {
        validateCookie(cookie);
        String value = getCookieValueWithOptions(cookie);
        List<String> allOtherCookieHeaders = getAllCookiesExcept(cookie.getName());
        allOtherCookieHeaders.add(cookie.getName() + "=" + value);
        response.headers().set("Set-Cookie", allOtherCookieHeaders);
    }

    private String getCookieValueWithOptions(Cookie cookie) {
        String value = cookie.getValue();
        if(!cookie.getOptions().isEmpty())
            value = value + printCookieOptions(cookie);
        return value;
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

    private void validateCookie(Cookie cookie) {
        String name = cookie.getName();
        String value = cookie.getValue();
        if(name.contains("=")) throw new IllegalArgumentException("Cookie name cannot contain the equals sign (=)");
        if(value == null) throw new IllegalArgumentException("Cookie value cannot be null");
        if(value.contains(",")) throw new IllegalArgumentException("Cookie value cannot contain commas (,)");
        if(value.contains(";")) throw new IllegalArgumentException("Cookie value cannot contain semicolons (;)");
        if(value.contains(" ")) throw new IllegalArgumentException("Cookie value cannot contain whitespaces");
    }

    private List<String> getAllCookiesExcept(String exceptionCookieName) {
        List<String> allCookieHeaders = new ArrayList<>(response.headers().getAll("Set-Cookie"));
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

    private String printCookieOptions(Cookie cookie) {
        String result = "";
        for(CookieOption option : cookie.getOptions())
            result += "; " + option.getValue();
        return result;
    }

}
