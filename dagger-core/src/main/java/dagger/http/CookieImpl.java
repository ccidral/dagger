package dagger.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CookieImpl implements Cookie {

    private final String name;
    private final String value;
    private final List<CookieOption> options = new ArrayList<>();

    public CookieImpl(String name, String value) {
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
    public List<CookieOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    @Override
    public void addOption(CookieOption option) {
        options.add(option);
    }

}
