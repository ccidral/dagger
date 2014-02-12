package dagger.servlet3;

import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.http.cookie.Cookie;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DaggerServletResponseTest {

    private HttpServletResponse httpServletResponse;
    private Response response;

    @Before
    public void setUp() throws Exception {
        httpServletResponse = mock(HttpServletResponse.class);
        response = new DaggerServletResponse(httpServletResponse);
    }

    @Test
    public void test_output_stream_is_the_same_as_the_http_servlet_response_output_stream() throws Exception {
        when(httpServletResponse.getOutputStream()).thenReturn(new MockServletOutputStream());
        assertSame(httpServletResponse.getOutputStream(), response.getOutputStream());
    }

    @Test
    public void test_set_status_code() {
        response.setStatusCode(StatusCode.NOT_MODIFIED);
        verify(httpServletResponse).setStatus(SC_NOT_MODIFIED);
    }

    @Test
    public void test_get_status_code() {
        when(httpServletResponse.getStatus()).thenReturn(SC_INTERNAL_SERVER_ERROR);
        assertEquals(StatusCode.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void test_set_header() {
        response.setHeader("Foo", "Bar");
        verify(httpServletResponse).setHeader("Foo", "Bar");
    }

    @Test
    public void test_set_cookie() {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn("Greeting");
        when(cookie.getValue()).thenReturn("Hello");

        response.setCookie(cookie);

        verify(httpServletResponse).addCookie(new ExpectedCookie("Greeting", "Hello"));
    }

    private class MockServletOutputStream extends ServletOutputStream {
        @Override
        public void write(int b) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException();
        }
    }


    private class ExpectedCookie extends javax.servlet.http.Cookie {

        public ExpectedCookie(String name, String value) {
            super(name, value);
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof javax.servlet.http.Cookie))
                return false;
            javax.servlet.http.Cookie anotherCookie = (javax.servlet.http.Cookie)obj;
            return getName().equals(anotherCookie.getName())
                && getValue().equals(anotherCookie.getValue());
        }

    }

}
