package dagger.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryStringImpl implements QueryString {

    Map<String, String> map = new HashMap<>();

    public QueryStringImpl(String queryString) {
        for(String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0].trim();
            boolean isEmptyValue = pair.endsWith("=");
            if(!key.isEmpty()) {
                String value = getValue(keyValue, isEmptyValue);
                String decodedValue = decode(value);
                map.put(key, decodedValue);
            }
        }
    }

    private String decode(String value) {
        if(value == null)
            return null;

        try {
            return URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
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
        return new QueryStringImpl(queryString);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public String get(String name) {
        return map.get(name);
    }

    @Override
    public Map<String, String> map() {
        return Collections.unmodifiableMap(map);
    }

}
