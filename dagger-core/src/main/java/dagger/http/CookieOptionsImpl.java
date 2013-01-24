package dagger.http;

public class CookieOptionsImpl implements CookieOptions {

    private boolean secure;
    private boolean httpOnly;

    @Override
    public String getOptionsString() {
        String result = "";
        if (secure) result += "Secure";
        if (httpOnly) {
            if(result.length() > 0)
                result += "; ";
            result += "HttpOnly";
        }
        return result.equals("") ? null : result;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

}
