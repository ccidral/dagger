package dagger.server.netty;

import dagger.http.Formats;
import dagger.http.HttpHeaderNames;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.lang.time.Clock;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NettyResponseTest {

    private static final Date CURRENT_TIME = timestamp(2012, Calendar.NOVEMBER, 15, 20, 45, 7);

    private MockNettyHttpResponse mockNettyHttpResponse;
    private Response response;
    private Clock mockClock;

    @Before
    public void setUp() throws Exception {
        mockNettyHttpResponse = new MockNettyHttpResponse();
        mockClock = mock(Clock.class);

        when(mockClock.now()).thenReturn(CURRENT_TIME);

        response = new NettyResponse(mockNettyHttpResponse, mockClock);
    }

    @Test
    public void testDateHeaderIsCreatedInTheConstructor() {
        String expectedDate = Formats.timestamp().format(CURRENT_TIME);
        assertEquals(expectedDate, mockNettyHttpResponse.getHeader(HttpHeaderNames.DATE));
    }

    @Test
    public void testOutputStreamIsNotNull() throws IOException {
        assertNotNull(response.getOutputStream());
    }

    @Test
    public void testWriteToOutputStream() throws IOException {
        response.getOutputStream().write("hello world".getBytes());

        assertNotNull(mockNettyHttpResponse.getContent());
        assertEquals("hello world", mockNettyHttpResponse.getWrittenText());
    }

    @Test
    public void testSetStatusCode() {
        response.setStatusCode(StatusCode.OK);

        assertNotNull(mockNettyHttpResponse.getStatus());
        assertEquals(StatusCode.OK.getNumber(), mockNettyHttpResponse.getStatus().getCode());
    }

    @Test
    public void testGetStatusCode() {
        response.setStatusCode(StatusCode.NOT_FOUND);
        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSetHeader() {
        response.setHeader("Fruit", "apple");
        assertEquals("apple", mockNettyHttpResponse.getHeader("Fruit"));
    }

    private static Date timestamp(int year, int month, int day, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hours, minutes, seconds);
        return calendar.getTime();
    }

}
