package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class ExpiresTest {

    @Test
    public void testGetValueFromBeginningOfJavaTime() {
        CookieOption option = new Expires(beginningOfJavaTime());
        assertEquals("Expires=Thu, 01-Jan-1970 00:00:00 GMT", option.getValue());
    }

    @Test
    public void testGetValueFromLastMomentOfYear() {
        CookieOption option = new Expires(lastSecondOfYear(2013));
        assertEquals("Expires=Tue, 31-Dec-2013 23:59:59 GMT", option.getValue());
    }

    private Date lastSecondOfYear(int year) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23 + (int)offset(calendar.getTimeZone()));
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private long offset(TimeZone timeZone) {
        long daylightSavingTime = hours(timeZone.getDSTSavings());
        long rawOffset = hours(timeZone.getRawOffset());
        return rawOffset + daylightSavingTime;
    }

    private long hours(long millis) {
        return millis / 1000 / 60 / 60;
    }

    private Date beginningOfJavaTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendar.setTimeInMillis(0);
        return calendar.getTime();
    }

}
