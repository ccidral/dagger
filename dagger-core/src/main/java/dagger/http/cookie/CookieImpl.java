package dagger.http.cookie;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CookieImpl implements Cookie {

    private static final DateFormat EXPIRES_OPTION_DATE_FORMAT;

    private final String name;
    private final String value;
    private boolean secure;
    private boolean httpOnly;
    private String path;
    private Integer maxAge;
    private Date expires;

    static {
        EXPIRES_OPTION_DATE_FORMAT = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.US);
        EXPIRES_OPTION_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public CookieImpl(String name, String value) {
        if(name.contains("=")) throw new IllegalArgumentException("Cookie name cannot contain the equals sign (=)");
        if(value == null) throw new IllegalArgumentException("Cookie value cannot be null");
        if(value.contains(",")) throw new IllegalArgumentException("Cookie value cannot contain commas (,)");
        if(value.contains(";")) throw new IllegalArgumentException("Cookie value cannot contain semicolons (;)");
        if(value.contains(" ")) throw new IllegalArgumentException("Cookie value cannot contain whitespaces");

        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean isHttpOnly() {
        return httpOnly;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public Integer getMaxAge() {
        return maxAge;
    }

    @Override
    public void setMaxAge(Integer seconds) {
        this.maxAge = seconds;
    }

    @Override
    public Date getExpires() {
        return expires;
    }

    @Override
    public void setExpires(Date date) {
        this.expires = date;
    }

    @Override
    public String print() {
        StringBuffer printed = new StringBuffer(name + "=" + value);
        if(secure) printed.append("; Secure");
        if (httpOnly) printed.append("; HttpOnly");
        if (path != null) printed.append("; Path="+path);
        if (maxAge != null) printed.append("; Max-Age=" + maxAge);
        if (expires != null) printed.append("; Expires=" + EXPIRES_OPTION_DATE_FORMAT.format(expires));
        return printed.toString();
    }

}
