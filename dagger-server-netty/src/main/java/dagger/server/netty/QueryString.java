package dagger.server.netty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryString {

    Map<String, String> map = new HashMap<>();

    public QueryString(String queryString) {
        for(String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0].trim();
            boolean isEmptyValue = pair.endsWith("=");
            if(!key.isEmpty()) {
                String value = getValue(keyValue, isEmptyValue);
                map.put(key, value);
            }
        }
    }

    private String getValue(String[] keyValue, boolean emptyValue) {
        if(keyValue.length > 1)
            return keyValue[1];

        if(emptyValue)
            return "";

        return null;
    }

    public static QueryString fromUri(String uri) {
        int questionMarkIndex = uri.indexOf('?');
        String queryString = questionMarkIndex > -1 ? uri.substring(questionMarkIndex + 1) : "";
        return new QueryString(queryString);
    }

    public int size() {
        return map.size();
    }

    public String get(String name) {
        return map.get(name);
    }

    public Map<String, String> map() {
        return Collections.unmodifiableMap(map);
    }

}
