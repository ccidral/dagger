package dagger.http;

import java.util.Collections;
import java.util.Map;

public class NullQueryString implements QueryString {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String get(String name) {
        return null;
    }

    @Override
    public Map<String, String> map() {
        return Collections.emptyMap();
    }

}
