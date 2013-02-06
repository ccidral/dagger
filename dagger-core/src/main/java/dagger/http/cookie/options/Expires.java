package dagger.http.cookie.options;

import dagger.http.cookie.CookieOption;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Expires implements CookieOption {

    private static final DateFormat DATE_FORMAT;

    private final Date expirationDate;

    static {
        DATE_FORMAT = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.US);
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public Expires(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String getValue() {
        return "Expires=" + DATE_FORMAT.format(expirationDate);
    }

}
