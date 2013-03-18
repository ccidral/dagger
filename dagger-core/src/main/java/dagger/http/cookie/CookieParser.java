package dagger.http.cookie;

import java.util.HashMap;
import java.util.Map;

public class CookieParser {

    public Map<String, String> parseCookies(String cookiesString) {
        if(cookiesString == null)
            return null;

        Map<String, String> map = new HashMap<>();
        for(String cookie : cookiesString.split("; ")) {
            int firstEqualSignPosition = cookie.indexOf("=");
            String name = cookie.substring(0, firstEqualSignPosition);
            String value = cookie.substring(firstEqualSignPosition + 1);
            map.put(name, nullIfEmptyString(value));
        }
        return map;
    }

    private String nullIfEmptyString(String value) {
        return "".equals(value) ? null : value;
    }

}
