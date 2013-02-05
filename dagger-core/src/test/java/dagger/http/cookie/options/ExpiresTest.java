package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class ExpiresTest {

    @Test
    public void testGetValue() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendar.setTimeInMillis(0);
        Date expirationDate = calendar.getTime();
        CookieOption option = new Expires(expirationDate);
        assertEquals("Expires=Thu, 01 Jan 1970 00:00:00 GMT", option.getValue());
    }

}
