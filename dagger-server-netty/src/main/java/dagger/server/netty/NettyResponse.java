package dagger.server.netty;

import dagger.http.Formats;
import dagger.http.HttpHeader;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;
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
        this.buffer = response.data();

        setHeader(HttpHeader.DATE, Formats.timestamp().format(clock.now()));
    }

    @Override
    public StatusCode getStatusCode() {
        return StatusCode.get(response.getStatus().code());
    }

    @Override
    public void setStatusCode(StatusCode statusCode) {
        response.setStatus(HttpResponseStatus.valueOf(statusCode.getCode()));
    }

    @Override
    public void setHeader(String name, String value) {
        response.headers().set(name, value);
    }

    @Override
    public void addCookie(Cookie cookie) {
        List<String> allOtherCookieHeaders = getAllCookiesExcept(cookie.getName());
        allOtherCookieHeaders.add(cookie.print());
        response.headers().set("Set-Cookie", allOtherCookieHeaders);
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

    private List<String> getAllCookiesExcept(String exceptionCookieName) {
        List<String> allCookieHeaders = new ArrayList<String>(response.headers().getAll("Set-Cookie"));
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
